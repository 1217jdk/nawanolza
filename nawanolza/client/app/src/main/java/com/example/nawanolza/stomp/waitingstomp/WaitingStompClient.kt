package com.example.nawanolza.stomp.waitingstomp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import com.example.nawanolza.LoginUtil
import com.example.nawanolza.MainActivity
import com.example.nawanolza.createGame.Waiting
import com.example.nawanolza.createGame.WaitingRvAdapter
import com.example.nawanolza.hideandseek.*
import com.example.nawanolza.retrofit.createroom.MemberList
import com.example.nawanolza.retrofit.enterroom.GetRoomResponse
import com.example.nawanolza.stomp.*
import com.google.gson.GsonBuilder
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.net.URL
import kotlin.concurrent.timer

class WaitingStompClient {
    companion object {
        val url = "wss://k7d103.p.ssafy.io/api/ws"
        val stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        var stopFlag = true
        lateinit var roomInfo: GetRoomResponse
        val markerMap: HashMap<Int, Marker> = HashMap()
        lateinit var winnerList: List<Winner>
        var winTagger = false

        // 마커 업데이트
//        fun updateMarker(naverMap: NaverMap, subGpsDto: SubGpsDto) {
//            if(markerMap.containsKey(subGpsDto.senderId))
//                markerMap.get(subGpsDto.senderId).marker = null
//        }

        // 소켓 연결
        fun connect() {
            stompClient.connect()
            stopFlag = false

            stompClient.lifecycle().subscribe { lifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.i("OPEND", "!!")
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        Log.i("CLOSED", "!!")
                    }
                    LifecycleEvent.Type.ERROR -> {
                        Log.i("ERROR", "!!")
                        Log.e("CONNECT ERROR", lifecycleEvent.exception.toString())

                    }
                    else ->{
                        Log.i("ELSE", lifecycleEvent.message)
                    }
                }
            }
            timer(initialDelay = 0L, period = 1000 * 50L) {
                Log.i("타이머 실행", "타이머 실행")
                stompClient.send("", "").subscribe()
                if (stopFlag)   cancel()
            }
        }


        // 연결 해제
        fun disconnect() {
            stompClient.disconnect()
            stopFlag = true
        }

        /** 받는 메서드 **/

        // 참가자 정보 받기
        fun subParticipate(entryCode: String, adapter: WaitingRvAdapter, activity: Activity){
            stompClient.topic("/sub/participate/$entryCode").subscribe { topicMessage ->
                Log.i("message Receive", topicMessage.payload)
                Waiting.memberList = GsonBuilder().create().fromJson(topicMessage.payload, MemberList::class.java).participants

                activity.runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }
        }

        // 게임 시작
        fun subGameStart(entryCode: String, context: Context) {
            stompClient.topic("/sub/game/start/$entryCode").subscribe{ topicMessage ->
                Log.i("message Receive", topicMessage.payload)


                roomInfo = GsonBuilder().create().fromJson(topicMessage.payload, GetRoomResponse::class.java)

                Waiting.memberList.add(0, Waiting.hostInfo)

                for(member in Waiting.memberList)
                    Waiting.memberHash.put(member.memberId, member)

                Waiting.tagger = roomInfo.tagger
                Waiting.runnerList = roomInfo.runners

                val intent = Intent(context, RoleCheckActivity::class.java)
                intent.putExtra("entryCode", entryCode)
                context.startActivity(intent)
            }
        }

        // 참여자 위치 받기 (구독)
        fun subGPS(entryCode: String, naverMap: NaverMap, activity: Activity, senderId: Int, adapter:HideSeekRvAdapter){
            println("구독했나요?")
            stompClient.topic("/sub/gps/$entryCode").subscribe{ topicMessage ->
                Log.i("subGPSMessage", topicMessage.payload)

                var subDto: SubGpsDto = GsonBuilder().create().fromJson(topicMessage.payload, SubGpsDto::class.java)
                println(subDto)

                if(senderId != subDto.senderId) {
                    val url = URL(Waiting.memberHash.get(subDto.senderId)!!.image)
                    val image = BitmapFactory.decodeStream(
                        url.openConnection().getInputStream()
                    )

                    activity.runOnUiThread {
                        if(markerMap.containsKey(subDto.senderId)) {
                            markerMap.get(subDto.senderId)?.map = null
                        }

                        if(!MainHideSeek.isTagger || MainHideSeek.isHintOn) {
                            val myLocation = LatLng(subDto.lat, subDto.lng)
                            val marker = Marker()
                            //마커

                            marker.position= myLocation
                            marker.icon = OverlayImage.fromBitmap(image)
                            marker.width  = 75
                            marker.height = 75

                            markerMap.put(subDto.senderId, marker)
                            marker.map = naverMap
//                    var timeFlag: Boolean = false

//                    timer(initialDelay = 0L, period = 1000L) {
//                        activity.runOnUiThread{
////                            marker.map = null
//                            timeFlag = true
//                        }
//                        if (timeFlag) cancel()
//                    }
                        }
                    }
                }
                Waiting.memberHash[subDto.senderId]?.location = roomInfo.range < MainHideSeek.DistanceManager.getDistance(subDto.lat, subDto.lng, roomInfo.lat, roomInfo.lng)
                activity.runOnUiThread{
                    adapter.notifyDataSetChanged()
                }

            }
        }

        // 잡힌 참여자 받기
        fun subEvent(entryCode: String, adapter:HideSeekRvAdapter, activity: Activity){
            stompClient.topic("/sub/event/$entryCode").subscribe{ topicMessage ->
                Log.i("message Receive", topicMessage.payload)
                val data = GsonBuilder().create().fromJson(topicMessage.payload, CatchResponse::class.java)
                Waiting.memberHash[data.catchMemberId]?.status = true
                Waiting.memberHash[data.catchMemberId]?.location = true
                MainHideSeek.caughtMember += 1

                activity.runOnUiThread {
                    adapter.notifyDataSetChanged()
                }

                if(MainHideSeek.caughtMember == Waiting.runnerList.size && MainHideSeek.isTagger) {
                    println("============check 똑같================")
                    MainHideSeek.finishGame()
                }
            }
        }

        // 게임 종료 받기
        fun subFinish(entryCode:String, context: Context) {
            stompClient.topic("/sub/game/finish/$entryCode").subscribe{ topicMessage ->
                Log.i("message Receive", topicMessage.payload)
                println("===========게임이 종료되었습니다=============")
                val data = GsonBuilder().create().fromJson(topicMessage.payload, FinishResponse::class.java)
                println(data)
                winnerList = data.winnerList
                winTagger = data.winTagger
                stompClient.disconnect()
                val intent = Intent(context, FinishGame::class.java)
                context.startActivity(intent)
            }
        }

        fun subChat(
            entryCode: String,
            memberId: Int,
            adapter: ChattingRvAdapter
        ) {
            stompClient.topic("/sub/chat/" + entryCode).subscribe { topicMessage ->
                val fromJson = GsonBuilder().create().fromJson(topicMessage.payload, SocketChatDTO::class.java)

                if(fromJson.senderId.toString() != memberId.toString()) {
                    adapter.addChat(ChatDTO(fromJson, ChatType.LEFT))
                }
            }
        }

        /** 보내는 메서드 **/

        // 내 위치 전송
        fun pubGPS(pubGpsRequest: PubGpsRequest) {
            val data = GsonBuilder().create().toJson(pubGpsRequest)
            stompClient.send("/pub/gps", data).subscribe()
        }

        //참가자 잡기
        fun pubEvent(pubEventRequest: PubEventRequest){
            val data = GsonBuilder().create().toJson(pubEventRequest)
            stompClient.send("/pub/event", data).subscribe()
        }
    }
}