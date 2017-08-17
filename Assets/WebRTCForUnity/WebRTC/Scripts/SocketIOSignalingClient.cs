using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace iBicha
{
	public class SocketIOSignalingClient : SignalingClient {
		#region implemented abstract members of SignalingClient
		public override bool SendMessage (Object data)
		{
			throw new System.NotImplementedException ();
		}
		public override bool SendOfferSDP (SessionDescription sdp)
		{
			throw new System.NotImplementedException ();
		}
		public override bool SendAnswerSDP (SessionDescription sdp)
		{
			throw new System.NotImplementedException ();
		}
		public override bool SendLocalIceCandidate (Object candidate)
		{
			throw new System.NotImplementedException ();
		}
		public override bool SendLocalIceCandidateRemovals (Object[] candidates)
		{
			throw new System.NotImplementedException ();
		}
		#endregion
	}
}
