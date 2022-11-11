package com.example.nawanolza.hideandseek

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nawanolza.LoginUtil
import com.example.nawanolza.R
import com.example.nawanolza.createGame.WaitingMember
import com.example.nawanolza.databinding.ActivityMainHideSeekBinding
import com.example.nawanolza.stomp.waitingstomp.WaitingStompClient
import com.google.android.gms.location.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_role_check_acitivity.*
import kotlinx.android.synthetic.main.activity_setting_hide_seek.*
import kotlinx.android.synthetic.main.fragment_participants.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainHideSeek : OnMapReadyCallback, AppCompatActivity() {

    // 네이버 맵 권한 요청
    private val permission_request = 99
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    var senderId = 0


    var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)

    lateinit var binding: ActivityMainHideSeekBinding
    lateinit var adapter: HideSeekRvAdapter
    lateinit var entryCode: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainHideSeekBinding.inflate(layoutInflater)
        setContentView(binding.root)
        entryCode = intent.getStringExtra("entryCode").toString()
        senderId = LoginUtil.getMember(this)?.id!!

        if (isPermitted()) {
            startProcess()
        } else {
            ActivityCompat.requestPermissions(this, permissions, permission_request)
        }//권한 확인


        updateTime()
        setRecycleView()

        binding.memberDetail.setOnClickListener {
            val intent = Intent(this@MainHideSeek, MemberDetail::class.java )
            startActivity(intent)
        }

    }

    private fun setRecycleView() {
        adapter = HideSeekRvAdapter(this)
        binding.mRecyclerView.adapter = adapter
        binding.mRecyclerView.layoutManager = GridLayoutManager(this, 4)
    }


    private fun updateTime() {
        println("third")
        println("===========checking========")


        val startTime = WaitingStompClient.roomInfo.startTime.slice(0..22)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")


        val start = LocalDateTime.parse(startTime, formatter)
        val end = start.plusMinutes(5)

        // 원래는 playTime/60 더해야함
        println(WaitingStompClient.roomInfo.playTime)
        println(end)

        val duration: Duration = Duration.between(LocalDateTime.now(), end)
        val seconds = duration.seconds

        println("==============check==============")
        println(seconds)

        /** 타이머 **/
        object : CountDownTimer(seconds*1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000).toInt() / 60
                val sec = (millisUntilFinished / 1000).toInt() % 60
                binding.tvTime.text = "$minutes:$sec"
            }
            override fun onFinish() {
                /** 끝났을 때 **/
                Toast.makeText(this@MainHideSeek, "끝", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    fun isPermitted(): Boolean {
        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }//권한을 허락 받아야함

    fun startProcess(){
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            } //권한
        mapFragment.getMapAsync(this)

        locationSource = FusedLocationSource(this, 1000) // 좌표 눌렀을때 현재 위치로 이동
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap){
        println("onMapReadyCheck")
        this.naverMap = naverMap

        val cameraPosition = CameraPosition(
            LatLng(36.1071562, 128.4164185),  // 위치 지정
            16.0 // 줌 레벨
        )
        naverMap.cameraPosition = cameraPosition

        val uiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = true //위치 버튼 활성화
        naverMap.locationSource = locationSource // 좌표 눌렀을때 현재 위치로 이동
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this) //gps 자동으로 받아오기
//        val marker = Marker()
//        marker.position = LatLng(36.1071562, 128.4164185)
//        marker.map = naverMap
        setLocationOverlay() // overlay 설정
        setUpdateLocationListener() //내위치를 가져오는 코드

//        naverMap.setOnMapClickListener { point, coord ->
//            val latLng = LatLng(coord.latitude, coord.longitude)
//            setPolyline(latLng)
//        }
        setPolyline(LatLng(WaitingStompClient.roomInfo.lat, WaitingStompClient.roomInfo.lng))

        WaitingStompClient.subGPS(entryCode, naverMap, this, senderId)
    }

    //내 위치를 가져오는 코드
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient //자동으로 gps값을 받아온다.
    lateinit var locationCallback: LocationCallback //gps응답 값을 가져온다.

    @SuppressLint("MissingPermission")
    fun setUpdateLocationListener() {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY //높은 정확도
            interval = 1000 //1초에 한번씩 GPS 요청
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for ((i, location) in locationResult.locations.withIndex()) {
                    Log.d("location: ", "${location.latitude}, ${location.longitude}")
//                    setLastLocation(location)

                    sendMyLocation(location)
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )

    }


    // 내 위치 보내기
    fun sendMyLocation(location: Location) {
        val pubGpsRequest = PubGpsRequest(entryCode, location.latitude, location.longitude, senderId, "GPS")
        WaitingStompClient.pubGPS(pubGpsRequest)
    }

    fun setLastLocation(location: Location) {
        val myLocation = LatLng(location.latitude, location.longitude)
        val marker = Marker()
        //마커
        val cameraUpdate = CameraUpdate.scrollTo(myLocation)
        naverMap.moveCamera(cameraUpdate)
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 5.0

        marker.position= myLocation
        marker.width  = 250
        marker.height = 250
//        marker.icon = OverlayImage.fromResource(MarkerImageUtil.getImage(current.characterId) as Int)
        marker.map = naverMap

//        marker.map = null
    }

    private fun setLocationOverlay(){
//        val myLocation = LatLng(location.latitude, location.longitude)
        val locationOverlay = naverMap.locationOverlay

        locationOverlay.position = LatLng(36.1071562, 128.4164185)
        locationOverlay.isVisible = true
    }

    //좌표간 거리 구하기
    private fun calDist(lat1:Double, lon1:Double, lat2:Double, lon2:Double) : Long{
        val EARTH_R = 6371000.0
        val rad = Math.PI / 180
        val radLat1 = rad * lat1
        val radLat2 = rad * lat2
        val radDist = rad * (lon1 - lon2)

        var distance = Math.sin(radLat1) * Math.sin(radLat2)
        distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist)
        val ret = EARTH_R * Math.acos(distance)

        return Math.round(ret) // 미터 단위
    }

    private fun setPolyline(latLng: LatLng) {

        val circle = CircleOverlay()
        circle.center = latLng
        val color = Color.parseColor("#ef5350")
        circle.outlineColor = color
        circle.outlineWidth = 1
        circle.radius = 100.0
        circle.map = naverMap
    }
}
