using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

namespace iBicha
{
	public class Room {

		public event Action<Participant> OnJoin;
		public event Action<Participant> OnLeave;

		public SignalingClient signalingClient { get; set;}

		public string RoomID {
			get {
				return uuid;
			}
		}

		private string uuid;

		public Room(string uuid) {
			this.uuid = uuid;
		}

		public void Join() {
			this.signalingClient.SendMessage (new UnityEngine.Object());
		}

		public void Leave() {

		}

		public class Participant
		{
			public string ID { get; set;}
			public VideoCapturer.CaptureSource VideoSource { get; set;}
			public bool IsAudioEnabled { get; set;}
		}
	}

}