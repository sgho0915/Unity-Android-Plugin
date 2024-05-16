# Unity Android Plugin
Android native module for Unity projetcs

## 이더넷 상태 체크(EthernetStatusPlugin)
- 이더넷 연결 상태 여부 bool형 리턴

## 와이파이 상태 체크(WiFiStatus)
- 와이파이 활성화/비활성화 여부 bool형 리턴
- 와이파이 SSID string형 리턴
- 와이파이 RSSI int형 리턴

## GPIO LED 제어(GPIOControl with jws api)
- GPIO read 결과 int형 리턴
- GPIO write 결과 int형 리턴

## RTSP 카메라 영상 스트리밍(RTSPPlayer)
- PlayRTSP CallStatic에 string형대 rtspUrl 입력 시 프레임 데이터 유니티 안드로이드 앱으로 반환

## 디바이스 저장소 용량 체크(StorageInfo)
- 전체 저장소 용량 string형 리턴
- 사용중 저장소 용량 string형 리턴
- 사용가능 저장소 용량 string형 리

## 앱 OTA 업데이트 시 앱 내 설치(UnityPlugin)
- apk 파일 설치
