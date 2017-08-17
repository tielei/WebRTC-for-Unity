using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using socket.io;
using System;
using UnityEngine.Events;

public class SocketIO : MonoBehaviour {
	public string serverUrl = "http://localhost:3000";

	public UnityEvent OnConnect;
	public UnityEventString OnError;
	public UnityEvent OnDisconnect;
	public UnityEventString OnChatMessage;


	private Socket socket;

	void Start () {
		socket = Socket.Connect(serverUrl);

		socket.On (SystemEvents.connect, onConnect);
		socket.On (SystemEvents.connectError, onError);
		socket.On (SystemEvents.disconnect, onDisconnect);

		socket.On("chat", onChatMessage);
	}

	public void Send(string message) {
		socket.Emit ("chat", message);
	}


	void onConnect() {
		Debug.Log("Connected");
		OnConnect.Invoke ();
	}

	void onError(Exception ex) {
		Debug.LogException(ex);
		OnError.Invoke (ex.ToString());
	}

	void onDisconnect() {
		Debug.Log("Disconnected");
		OnDisconnect.Invoke ();
	}

	void onChatMessage(string message) {
		ChatMessage cm = JsonUtility.FromJson<ChatMessage> (message);
		OnChatMessage.Invoke (cm.ToString());
	}

	class ChatMessage
	{
		public string id;
		public string msg;

		public ChatMessage(string id, string msg) {
			this.id = id;
			this.msg = msg;
		}

		public override string ToString ()
		{
			return string.Format ("user#{0}: {1}", id, msg);
		}
	}

}
