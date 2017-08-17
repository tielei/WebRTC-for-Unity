using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;


namespace iBicha
{
	public abstract class WebRTC {
		public enum VideoCodec 
		{
			VP8,
			VP9,
			H264
		}

		public enum AudioCodec
		{
			opus,
			ISAC
		}


		private static WebRTC instance;
		public static WebRTC Instance {
			get {
				if (instance == null) {
					#if UNITY_ANDROID
					instance = new WebRTCAndroid();
					#endif
				}
				return instance;
			}
		}
	}
}
