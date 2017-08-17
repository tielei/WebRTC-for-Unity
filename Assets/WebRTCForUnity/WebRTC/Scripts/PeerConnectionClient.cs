using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

namespace iBicha
{
	public class PeerConnectionClient {
		public event Action OnConnected;
		public event Action OnDisconnected;

	
		public List<VideoCapturer.CaptureSource> VideoSources { get;}
		public bool AudioEnabled { get; set; }


		public PeerConnectionClient() {
			VideoSources = new List<VideoCapturer.CaptureSource> ();
			AudioEnabled = true;
		}


		void CreateOffer() {

		}

		void CreateAnswer() {

		}

		void SetLocalDescription() {

		}

		void SetRemoteDescription() {

		}

	
	}
}
