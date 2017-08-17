using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace iBicha
{
	public abstract class SignalingClient {

		public abstract bool SendMessage (Object data);

		public abstract bool SendOfferSDP (SessionDescription sdp);

		public abstract bool SendAnswerSDP (SessionDescription sdp);

		public abstract bool SendLocalIceCandidate (Object candidate);

		public abstract bool SendLocalIceCandidateRemovals (Object[] candidates);

	}
}
