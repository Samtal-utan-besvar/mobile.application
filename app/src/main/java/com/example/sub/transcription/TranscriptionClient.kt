package com.example.sub.transcription

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import com.google.android.material.internal.ContextUtils.getActivity
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

private const val TRANSCRIPT_URL = "ws://129.151.206.9:6000" // use local ip for devices in local network

class TranscriptionClient(private var context: Context) {

    private var webSocket: WebSocket? = null
    val transcriptionListeners = ArrayList<TranscriptionListener>()


    init{
        connect()
    }


    fun connect(){
        // wss test
        val client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .pingInterval(5, TimeUnit.SECONDS)
            //.sslSocketFactory()
            .build()
        val request = Request.Builder()
            .url(TRANSCRIPT_URL) // 'ws'
            .build()
        val wsListener = TranscribingWebSocketListener()
        webSocket = client.newWebSocket(request, wsListener) // this provide to make 'Open ws connection'
    }


    fun close(){
        webSocket?.close(1000, "Hang up")
        transcriptionListeners.clear()
    }


    private inner class TranscribingWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WS-Listener: ", "Websocket Opened!")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("Transcription-receive", text)

            Log.d("Answer", text)
            val list = text.split(":")
            val id = list[0]
            val data = list[1]

            transcriptionListeners.forEach {
                it.onTranscriptionComplete(id, data)
            }
        }


        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(code, reason)
            super.onClosed(webSocket, code, reason)
            Log.d("Transcription-closed", reason)
        }


        @SuppressLint("RestrictedApi")//this suprress is for getActivity(context) which has a known lint-error
        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            val msg = response?.message ?: t.message ?: ""
            Log.d("Transcription-fail", msg)

            getActivity(context)?.runOnUiThread(java.lang.Runnable{

                val builder = AlertDialog.Builder(context)
                //set title for alert dialog
                builder.setTitle("Transkriberingsfel")

                //set message for alert dialog
                builder.setMessage(
                    "Du har tappat kontakten med transkriberingservern, kontrollera din internetanslutning och starta om samtalet för att återuppta kontakten."
                )
                builder.setIcon(android.R.drawable.ic_dialog_alert)

                //performing cancel action
                builder.setNeutralButton("Okej") { dialogInterface, which -> }

                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(false)
                alertDialog.show()
            })


        }
    }


    private fun send(jsonString: String){
        webSocket?.send(jsonString)
    }


    fun sendAnswer(id: String, ownertype: String) {
        Log.d("Transcription-send_answer", ownertype)
        val message = JSONObject()
        message.put("Reason", "answer")
        message.put("Id", id)
        message.put("Data", ownertype)
        val msgList = listOf(message)
        send(msgList.toString())
    }


    fun sendSound(id: String, sound: ByteArray) {
        Log.d("Transcription-send-sound", "sound")
        //convert sound to string here()
        val message = JSONObject()
        message.put("Reason", "transcription")
        message.put("Id", id)
        message.put("Data", sound.toString(Charsets.ISO_8859_1))
        val msgList = listOf(message)
        send(msgList.toString())
    }

}