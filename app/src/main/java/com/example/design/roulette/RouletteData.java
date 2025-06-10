package com.example.design.roulette;

import com.example.design.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouletteData {

    // 지역별 여행지 데이터를 매핑합니다.
    // 각 여행지는 "여행지 이름"과 해당 여행지를 나타내는 "이미지 리소스 ID"를 가집니다.
    public static final Map<String, List<TravelDestination>> REGION_TO_DESTINATIONS = new HashMap<>();

    // 각 여행지별 장소 데이터를 매핑합니다.
    // 각 장소는 "장소 이름"을 가집니다.
    public static final Map<String, List<Place>> DESTINATION_TO_PLACES = new HashMap<>();

    static {
        // ========== 전라북도 ==========
        REGION_TO_DESTINATIONS.put("전라북도", Arrays.asList(
                new TravelDestination("전주", R.drawable.jb1),
                new TravelDestination("군산", R.drawable.jb2),
                new TravelDestination("부안", R.drawable.jb3)
        ));
        DESTINATION_TO_PLACES.put("전주", Arrays.asList(
                new Place("전주 한옥마을"),
                new Place("객리단길"),
                new Place("남부시장"),
                new Place("덕진공원"),
                new Place("전주 동물원")
        ));
        DESTINATION_TO_PLACES.put("군산", Arrays.asList(
                new Place("은파호수공원"),
                new Place("경암동 철길마을"),
                new Place("새만금 방조제"),
                new Place("선유도"),
                new Place("초원사진관")
        ));
        DESTINATION_TO_PLACES.put("부안", Arrays.asList(
                new Place("채석강"),
                new Place("내소사"),
                new Place("변산반도 국립공원"),
                new Place("곰소염전"),
                new Place("부안 영상테마파크")
        ));


        // ========== 제주도 ==========
        REGION_TO_DESTINATIONS.put("제주도", Arrays.asList(
                new TravelDestination("제주시", R.drawable.jj1),
                new TravelDestination("서귀포", R.drawable.jj2),
                new TravelDestination("우도", R.drawable.jj3)
        ));
        DESTINATION_TO_PLACES.put("제주시", Arrays.asList(
                new Place("동문시장"),
                new Place("용두암"),
                new Place("한라산 국립공원"),
                new Place("애월읍"),
                new Place("제주공항 근처")
        ));
        DESTINATION_TO_PLACES.put("서귀포", Arrays.asList(
                new Place("천지연폭포"),
                new Place("정방폭포"),
                new Place("중문관광단지"),
                new Place("성산일출봉"),
                new Place("섭지코지")
        ));
        DESTINATION_TO_PLACES.put("우도", Arrays.asList(
                new Place("서빈백사"),
                new Place("우도봉"),
                new Place("검멀레 해변"),
                new Place("하고수동 해변"),
                new Place("땅콩 아이스크림")
        ));


        // ========== 경상북도 ==========
        REGION_TO_DESTINATIONS.put("경상북도", Arrays.asList(
                new TravelDestination("경주", R.drawable.gb1),
                new TravelDestination("포항", R.drawable.gb2),
                new TravelDestination("안동", R.drawable.gb3)
        ));
        DESTINATION_TO_PLACES.put("경주", Arrays.asList(
                new Place("불국사"),
                new Place("첨성대"),
                new Place("대릉원"),
                new Place("동궁과 월지"),
                new Place("황리단길")
        ));
        DESTINATION_TO_PLACES.put("포항", Arrays.asList(
                new Place("호미곶"),
                new Place("영일대 해수욕장"),
                new Place("구룡포 근대문화거리"),
                new Place("죽도시장"),
                new Place("환호공원")
        ));
        DESTINATION_TO_PLACES.put("안동", Arrays.asList(
                new Place("하회마을"),
                new Place("월영교"),
                new Place("도산서원"),
                new Place("봉정사"),
                new Place("찜닭 골목")
        ));


        // ========== 경상남도 ==========
        REGION_TO_DESTINATIONS.put("경상남도", Arrays.asList(
                new TravelDestination("통영", R.drawable.gn1),
                new TravelDestination("거제", R.drawable.gn2),
                new TravelDestination("남해", R.drawable.gn3)
        ));
        DESTINATION_TO_PLACES.put("통영", Arrays.asList(
                new Place("동피랑 마을"),
                new Place("이순신 공원"),
                new Place("루지"),
                new Place("서피랑 마을"),
                new Place("강구안")
        ));
        DESTINATION_TO_PLACES.put("거제", Arrays.asList(
                new Place("외도 보타니아"),
                new Place("바람의 언덕"),
                new Place("학동 몽돌해변"),
                new Place("매미성"),
                new Place("해금강")
        ));
        DESTINATION_TO_PLACES.put("남해", Arrays.asList(
                new Place("독일마을"),
                new Place("다랭이마을"),
                new Place("보리암"),
                new Place("상주은모래비치"),
                new Place("금산")
        ));


        // ========== 충청남도 ==========
        REGION_TO_DESTINATIONS.put("충청남도", Arrays.asList(
                new TravelDestination("공주", R.drawable.cn1),
                new TravelDestination("부여", R.drawable.cn2),
                new TravelDestination("태안", R.drawable.cn3)
        ));
        DESTINATION_TO_PLACES.put("공주", Arrays.asList(
                new Place("공산성"),
                new Place("무령왕릉과 왕릉원"),
                new Place("마곡사"),
                new Place("공주 한옥마을"),
                new Place("금강")
        ));
        DESTINATION_TO_PLACES.put("부여", Arrays.asList(
                new Place("부여 능산리 고분군"),
                new Place("정림사지 5층 석탑"),
                new Place("궁남지"),
                new Place("부소산성 낙화암"),
                new Place("국립부여박물관")
        ));
        DESTINATION_TO_PLACES.put("태안", Arrays.asList(
                new Place("만리포 해수욕장"),
                new Place("꽃지 해변"),
                new Place("신두리 해안사구"),
                new Place("천리포수목원"),
                new Place("안면도")
        ));


        // ========== 충청북도 ==========
        REGION_TO_DESTINATIONS.put("충청북도", Arrays.asList(
                new TravelDestination("단양", R.drawable.cb1),
                new TravelDestination("청주", R.drawable.cb2),
                new TravelDestination("제천", R.drawable.cb3)
        ));
        DESTINATION_TO_PLACES.put("단양", Arrays.asList(
                new Place("도담삼봉"),
                new Place("만천하스카이워크"),
                new Place("고수동굴"),
                new Place("단양구경시장"),
                new Place("패러글라이딩")
        ));
        DESTINATION_TO_PLACES.put("청주", Arrays.asList(
                new Place("청남대"),
                new Place("상당산성"),
                new Place("수암골 벽화마을"),
                new Place("오송역"),
                new Place("청주국제공항")
        ));
        DESTINATION_TO_PLACES.put("제천", Arrays.asList(
                new Place("의림지"),
                new Place("청풍호반 케이블카"),
                new Place("박달재"),
                new Place("리솜포레스트"),
                new Place("자연치유 도시")
        ));


        // ========== 전라남도 ==========
        REGION_TO_DESTINATIONS.put("전라남도", Arrays.asList(
                new TravelDestination("여수", R.drawable.jn1),
                new TravelDestination("순천", R.drawable.jn2),
                new TravelDestination("목포", R.drawable.jn3)
        ));
        DESTINATION_TO_PLACES.put("여수", Arrays.asList(
                new Place("여수 밤바다"),
                new Place("오동도"),
                new Place("해상 케이블카"),
                new Place("돌산공원"),
                new Place("낭만포차")
        ));
        DESTINATION_TO_PLACES.put("순천", Arrays.asList(
                new Place("순천만 국가정원"),
                new Place("순천만 습지"),
                new Place("낙안읍성"),
                new Place("드라마 세트장"),
                new Place("선암사")
        ));
        DESTINATION_TO_PLACES.put("목포", Arrays.asList(
                new Place("목포 해상케이블카"),
                new Place("갓바위"),
                new Place("목포 근대역사관"),
                new Place("유달산"),
                new Place("연희네 슈퍼")
        ));


        // ========== 경기도 ==========
        REGION_TO_DESTINATIONS.put("경기도", Arrays.asList(
                new TravelDestination("수원", R.drawable.gg1),
                new TravelDestination("가평", R.drawable.gg2),
                new TravelDestination("용인", R.drawable.gg3)
        ));
        DESTINATION_TO_PLACES.put("수원", Arrays.asList(
                new Place("수원화성"),
                new Place("화성행궁"),
                new Place("행리단길"),
                new Place("지동시장"),
                new Place("광교호수공원")
        ));
        DESTINATION_TO_PLACES.put("가평", Arrays.asList(
                new Place("남이섬"),
                new Place("쁘띠프랑스"),
                new Place("아침고요수목원"),
                new Place("제이드가든"),
                new Place("캠핑")
        ));
        DESTINATION_TO_PLACES.put("용인", Arrays.asList(
                new Place("에버랜드"),
                new Place("한국민속촌"),
                new Place("캐리비안 베이"),
                new Place("용인 자연휴양림"),
                new Place("백남준 아트센터")
        ));


        // ========== 강원도 ==========
        REGION_TO_DESTINATIONS.put("강원도", Arrays.asList(
                new TravelDestination("속초", R.drawable.gy1),
                new TravelDestination("강릉", R.drawable.gy2),
                new TravelDestination("춘천", R.drawable.gy3)
        ));
        DESTINATION_TO_PLACES.put("속초", Arrays.asList(
                new Place("속초 해수욕장"),
                new Place("설악산 국립공원"),
                new Place("아바이마을"),
                new Place("속초 중앙시장"),
                new Place("영금정")
        ));
        DESTINATION_TO_PLACES.put("강릉", Arrays.asList(
                new Place("강릉 커피거리"),
                new Place("정동진"),
                new Place("오죽헌"),
                new Place("안목 해변"),
                new Place("경포대")
        ));
        DESTINATION_TO_PLACES.put("춘천", Arrays.asList(
                new Place("남이섬"),
                new Place("소양강 스카이워크"),
                new Place("김유정 문학촌"),
                new Place("제이드가든"),
                new Place("닭갈비 골목")
        ));


        // 기본값 (region이 null이거나 매칭되지 않을 경우)
        // 예를 들어 제주도로 기본 여행지를 설정합니다.
        REGION_TO_DESTINATIONS.put("default", Arrays.asList(
                new TravelDestination("제주", R.drawable.jeju)
        ));
        DESTINATION_TO_PLACES.put("제주", Arrays.asList(
                new Place("성산일출봉"),
                new Place("한라산"),
                new Place("천지연폭포"),
                new Place("동문시장")
        ));

    }

    public static class TravelDestination {
        public String name;
        public int imageResId;

        public TravelDestination(String name, int imageResId) {
            this.name = name;
            this.imageResId = imageResId;
        }
    }

    public static class Place {
        public String name;
        public boolean isChecked; // 체크박스 상태 저장을 위한 필드

        public Place(String name) {
            this.name = name;
            this.isChecked = false; // 기본값은 체크되지 않음
        }
    }

    // region에 해당하는 여행지 목록을 반환
    public static List<TravelDestination> getDestinationsForRegion(String region) {
        return REGION_TO_DESTINATIONS.getOrDefault(region, REGION_TO_DESTINATIONS.get("default"));
    }

    // destination에 해당하는 장소 목록을 반환
    public static List<Place> getPlacesForDestination(String destinationName) {
        return DESTINATION_TO_PLACES.getOrDefault(destinationName, Collections.emptyList());
    }
}