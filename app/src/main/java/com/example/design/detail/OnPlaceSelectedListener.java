// OnPlaceSelectedListener.java
package com.example.design.detail;

// 선택된 장소 이름을 Fragment로 다시 전달하기 위한 인터페이스
public interface OnPlaceSelectedListener {
    /**
     * 장소 선택 다이얼로그를 띄우거나, 선택된 장소 이름을 Fragment로 전달하는 콜백 메서드입니다.
     * @param placeName 선택된 장소 이름. 다이얼로그를 띄우는 요청일 경우 null입니다.
     * @param dayPosition 이 콜백을 요청한 DayFragment의 뷰페이저 내 인덱스입니다.
     */
    void onPlaceSelected(String placeName, int dayPosition);
}