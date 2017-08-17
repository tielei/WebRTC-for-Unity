using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

[RequireComponent(typeof(SocketIO))]
public class SocketIO_UI : MonoBehaviour {
	public Text log;
	public InputField input;

	private SocketIO socketio;

	void Start() {
		socketio = GetComponent<SocketIO> ();
	}

	public void Send() {
		socketio.Send (input.text);
		input.text = "";
	}


	public void OnMessage(string msg) {
		log.text += msg + Environment.NewLine;
	}

}
