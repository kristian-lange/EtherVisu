#include<stdio.h>
//#include<string.h>
//#include<stdlib.h>
#include<pcap/pcap.h>
//#include<errno.h>
//#include<inet.h>
//#include<ethernet.h>
//#include<wireless.h>
//#include<if_ether.h>
#include<jni.h>
#include "netvisu_net_SSIDMonitor.h"

typedef struct mac_header
{
  unsigned char fc[2];
  unsigned char id[2];
  unsigned char add1[6];
  unsigned char add2[6];
  unsigned char add3[6];
  unsigned char sc[2];
} mac_header;

typedef struct frame_control
{
  unsigned protocol :2;
  unsigned type :2;
  unsigned subtype :4;
  unsigned to_ds :1;
  unsigned from_ds :1;
  unsigned more_frag :1;
  unsigned retry :1;
  unsigned pwr_mgt :1;
  unsigned more_data :1;
  unsigned wep :1;
  unsigned order :1;
} frame_control;

typedef struct beacon_header
{
  unsigned char timestamp[8];
  unsigned char beacon_interval[2];
  unsigned char cap_info[2];
} beacon_header;

JNIEnv *_env;
jobject _obj;
jmethodID _mid;

void procPacket(u_char *arg, const struct pcap_pkthdr *pkthdr, const u_char *packet)
{
  int RADIO_HEADER_SIZE = 18;
  char * temp;
  char ssid[32];
  jstring jstr_dest_add;
  jstring jstr_source_add;
  jstring jstr_bssid;
  jstring jstr_ssid;
  struct mac_header *p= (struct mac_header *)(packet + RADIO_HEADER_SIZE);
  struct frame_control *control = (struct frame_control *) p->fc;
  temp = (char *) (packet + sizeof (struct mac_header) +sizeof (struct beacon_header) + RADIO_HEADER_SIZE);
  if ((control->protocol == 0) && (control->type == 0) && (control->subtype == 8) )  // beacon frame
    {
    memset(ssid,0,32);
    memcpy (ssid, &temp[2], temp[1]);
    //printf ("Destination Add : %s\n", ether_ntoa ((struct ether_addr *)p->add1));
    //printf ("Source Add : %s\n", ether_ntoa ((struct ether_addr *)p->add2));
    //printf ("BSSID : %s\n", ether_ntoa ((struct ether_addr *)p->add3));
    //printf ("ssid = %s\n", ssid);
    jstr_dest_add = (*_env)->NewStringUTF(_env, ether_ntoa ((struct ether_addr *)p->add1));
    jstr_source_add = (*_env)->NewStringUTF(_env, ether_ntoa ((struct ether_addr *)p->add2));
    jstr_bssid = (*_env)->NewStringUTF(_env, ether_ntoa ((struct ether_addr *)p->add3));
    jstr_ssid = (*_env)->NewStringUTF(_env, ssid);
    (*_env)->CallVoidMethod(_env, _obj, _mid, jstr_dest_add, jstr_source_add, jstr_bssid, jstr_ssid);
    }
}

JNIEXPORT void JNICALL Java_ethervisu_monitors_PcapMonitor_runMonitor(JNIEnv *env, jobject obj)
{
  printf("\nStart Monitor ...");
  jclass cls = (*env)->GetObjectClass(env, obj);
  jmethodID mid = (*env)->GetMethodID(env, cls, "nextPacket",
      "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
  if (mid == NULL)
    {
    return; /* method not found */
    }
  _env = env;
  _obj = obj;
  _mid = mid;

  char *dev = "wlan0";
  char errbuf[PCAP_ERRBUF_SIZE];
  pcap_t *handle;
  printf("\nInitialising capture interface ...");
  //pcap initialisation
  handle = pcap_open_live(dev, BUFSIZ, 1, -1, errbuf);
  if (handle == NULL)
  {
    printf("pcap_open_live : %s\n", errbuf);
    exit(1);
  }
  printf("\nStarting Capture ...\n");
  // tell pcap to pass on captures frames to our packet_decoder fn
  pcap_loop(handle, -1, procPacket, NULL);
  return;
}

