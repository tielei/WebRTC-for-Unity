﻿using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace iBicha
{
	public class CameraCapture {
		
		public static void StartCameraCapture(bool frontCamera ,ScreenCaptureCallback callback)
		{
			ThreadUtils.RunOnUpdate (()=>{
				WebRTCAndroid.WebRTC_JavaClass.CallStatic("StartCameraCapture", frontCamera, callback);
			});
		}
	}

}
