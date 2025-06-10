package com.example.design.roulette;

import com.example.design.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouletteData {

    public static final Map<String, List<TravelDestination>> REGION_TO_DESTINATIONS = new HashMap<>();
    public static final Map<String, List<Place>> DESTINATION_TO_PLACES = new HashMap<>();

    static {
        // ========== 전라북도 ==========
        REGION_TO_DESTINATIONS.put("전라북도", Arrays.asList(
                new TravelDestination("전주", R.drawable.jb1),
                new TravelDestination("군산", R.drawable.jb2),
                new TravelDestination("부안", R.drawable.jb3)
        ));
        DESTINATION_TO_PLACES.put("전주", Arrays.asList(
                // 모든 장소 이미지 리소스를 R.drawable.jb1로 변경
                new Place("전주 한옥마을", R.drawable.jb1, "명소", 4.7f, "전주시 완산구 태조로 44"),
                new Place("객리단길", R.drawable.jb1, "거리/상점가", 4.3f, "전주시 완산구 전주객사2길"),
                new Place("남부시장", R.drawable.jb1, "시장/음식", 4.4f, "전주시 완산구 풍남문2길 63"),
                new Place("덕진공원", R.drawable.jb1, "공원/자연", 4.2f, "전주시 덕진구 권삼득로 390"),
                new Place("전주 동물원", R.drawable.jb1, "동물원", 3.9f, "전주시 덕진구 소리로 68")
        ));
        DESTINATION_TO_PLACES.put("군산", Arrays.asList(
                new Place("은파호수공원", R.drawable.jb1, "공원/자연", 4.5f, "군산시 은파순환길"),
                new Place("경암동 철길마을", R.drawable.jb1, "명소", 4.3f, "군산시 경촌4길 14"),
                new Place("새만금 방조제", R.drawable.jb1, "명소", 4.0f, "군산시 옥도면 신시도리"),
                new Place("선유도", R.drawable.jb1, "섬/해변", 4.6f, "군산시 옥도면 선유도리"),
                new Place("초원사진관", R.drawable.jb1, "역사/문화", 4.1f, "군산시 구영2길 12-1")
        ));
        DESTINATION_TO_PLACES.put("부안", Arrays.asList(
                new Place("채석강", R.drawable.jb1, "자연", 4.7f, "부안군 변산면 격포리"),
                new Place("내소사", R.drawable.jb1, "사찰/역사", 4.5f, "부안군 진서면 내소사로 243"),
                new Place("변산반도 국립공원", R.drawable.jb1, "국립공원", 4.6f, "부안군 변산면 변산해변로"),
                new Place("곰소염전", R.drawable.jb1, "체험/명소", 4.2f, "부안군 진서면 곰소리"),
                new Place("부안 영상테마파크", R.drawable.jb1, "테마파크", 3.8f, "부안군 변산면 격포리 318")
        ));


        // ========== 제주도 ==========
        REGION_TO_DESTINATIONS.put("제주도", Arrays.asList(
                new TravelDestination("제주시", R.drawable.jj1),
                new TravelDestination("서귀포", R.drawable.jj2),
                new TravelDestination("우도", R.drawable.jj3)
        ));
        DESTINATION_TO_PLACES.put("제주시", Arrays.asList(
                new Place("동문시장", R.drawable.jb1, "시장/음식", 4.3f, "제주시 관덕로14길 20"),
                new Place("용두암", R.drawable.jb1, "명소", 4.1f, "제주시 용두암길 15"),
                new Place("한라산 국립공원", R.drawable.jb1, "자연", 4.9f, "제주시 1100로 2070-61"),
                new Place("애월읍", R.drawable.jb1, "카페거리", 4.5f, "제주시 애월읍 애월로"),
                new Place("제주공항 근처", R.drawable.jb1, "기타", 3.5f, "제주시 공항로 2")
        ));
        DESTINATION_TO_PLACES.put("서귀포", Arrays.asList(
                new Place("천지연폭포", R.drawable.jb1, "자연", 4.6f, "서귀포시 천지연로 663"),
                new Place("정방폭포", R.drawable.jb1, "자연", 4.5f, "서귀포시 칠십리로214번길 37"),
                new Place("중문관광단지", R.drawable.jb1, "관광단지", 4.4f, "서귀포시 중문관광로 93"),
                new Place("성산일출봉", R.drawable.jb1, "자연", 4.8f, "서귀포시 성산읍 성산리 1"),
                new Place("섭지코지", R.drawable.jb1, "해안절경", 4.5f, "서귀포시 성산읍 섭지코지로")
        ));
        DESTINATION_TO_PLACES.put("우도", Arrays.asList(
                new Place("서빈백사", R.drawable.jb1, "해변", 4.7f, "제주시 우도면 연평리"),
                new Place("우도봉", R.drawable.jb1, "자연", 4.6f, "제주시 우도면 우도봉길"),
                new Place("검멀레 해변", R.drawable.jb1, "해변", 4.4f, "제주시 우도면 우도해안길"),
                new Place("하고수동 해변", R.drawable.jb1, "해변", 4.3f, "제주시 우도면 하고수동길"),
                new Place("땅콩 아이스크림", R.drawable.jb1, "음식", 4.0f, "제주시 우도면 일대")
        ));


        // ========== 경상북도 ==========
        REGION_TO_DESTINATIONS.put("경상북도", Arrays.asList(
                new TravelDestination("경주", R.drawable.gb1),
                new TravelDestination("포항", R.drawable.gb2),
                new TravelDestination("안동", R.drawable.gb3)
        ));
        DESTINATION_TO_PLACES.put("경주", Arrays.asList(
                new Place("불국사", R.drawable.jb1, "사찰/역사", 4.8f, "경주시 불국로 385"),
                new Place("첨성대", R.drawable.jb1, "유적", 4.6f, "경주시 첨성로 140-25"),
                new Place("대릉원", R.drawable.jb1, "유적", 4.7f, "경주시 계림로 9"),
                new Place("동궁과 월지", R.drawable.jb1, "유적", 4.7f, "경주시 원화로 102"),
                new Place("황리단길", R.drawable.jb1, "거리/상점가", 4.4f, "경주시 포석로1083번길 18")
        ));
        DESTINATION_TO_PLACES.put("포항", Arrays.asList(
                new Place("호미곶", R.drawable.jb1, "자연", 4.5f, "포항시 남구 호미곶면 해맞이로 150"),
                new Place("영일대 해수욕장", R.drawable.jb1, "해변", 4.3f, "포항시 북구 해안로 165"),
                new Place("구룡포 근대문화거리", R.drawable.jb1, "역사/문화", 4.2f, "포항시 남구 구룡포읍 구룡포길 153"),
                new Place("죽도시장", R.drawable.jb1, "시장", 4.0f, "포항시 북구 죽도시장길 13"),
                new Place("환호공원", R.drawable.jb1, "공원", 4.1f, "포항시 북구 환호동 280")
        ));
        DESTINATION_TO_PLACES.put("안동", Arrays.asList(
                new Place("하회마을", R.drawable.jb1, "유적", 4.8f, "안동시 풍천면 하회종가길 40"),
                new Place("월영교", R.drawable.jb1, "명소", 4.5f, "안동시 상아동 569"),
                new Place("도산서원", R.drawable.jb1, "사찰/역사", 4.6f, "안동시 도산면 토계리 680"),
                new Place("봉정사", R.drawable.jb1, "사찰/역사", 4.4f, "안동시 서후면 봉정사길 220"),
                new Place("찜닭 골목", R.drawable.jb1, "음식", 4.2f, "안동시 운흥동 안동구시장 내")
        ));


        // ========== 경상남도 ==========
        REGION_TO_DESTINATIONS.put("경상남도", Arrays.asList(
                new TravelDestination("통영", R.drawable.gn1),
                new TravelDestination("거제", R.drawable.gn2),
                new TravelDestination("남해", R.drawable.gn3)
        ));
        DESTINATION_TO_PLACES.put("통영", Arrays.asList(
                new Place("동피랑 마을", R.drawable.jb1, "명소", 4.5f, "통영시 동피랑1길 6"),
                new Place("이순신 공원", R.drawable.jb1, "공원", 4.4f, "통영시 정량동"),
                new Place("루지", R.drawable.jb1, "체험", 4.6f, "통영시 발개로 178"),
                new Place("서피랑 마을", R.drawable.jb1, "명소", 4.3f, "통영시 서피랑3길"),
                new Place("강구안", R.drawable.jb1, "항구", 4.2f, "통영시 강구안길")
        ));
        DESTINATION_TO_PLACES.put("거제", Arrays.asList(
                new Place("외도 보타니아", R.drawable.jb1, "섬/정원", 4.8f, "거제시 일운면 외도길 17"),
                new Place("바람의 언덕", R.drawable.jb1, "자연", 4.7f, "거제시 남부면 갈곶리"),
                new Place("학동 몽돌해변", R.drawable.jb1, "해변", 4.5f, "거제시 동부면 학동리"),
                new Place("매미성", R.drawable.jb1, "명소", 4.6f, "거제시 장목면 복항길"),
                new Place("해금강", R.drawable.jb1, "자연", 4.7f, "거제시 남부면 갈곶리")
        ));
        DESTINATION_TO_PLACES.put("남해", Arrays.asList(
                new Place("독일마을", R.drawable.jb1, "명소", 4.4f, "남해군 삼동면 독일로"),
                new Place("다랭이마을", R.drawable.jb1, "명소", 4.6f, "남해군 남면 남서대로"),
                new Place("보리암", R.drawable.jb1, "사찰/명소", 4.7f, "남해군 상주면 보리암로 665"),
                new Place("상주은모래비치", R.drawable.jb1, "해변", 4.3f, "남해군 상주면 상주리"),
                new Place("금산", R.drawable.jb1, "자연", 4.5f, "남해군 상주면 상주리")
        ));


        // ========== 충청남도 ==========
        REGION_TO_DESTINATIONS.put("충청남도", Arrays.asList(
                new TravelDestination("공주", R.drawable.cn1),
                new TravelDestination("부여", R.drawable.cn2),
                new TravelDestination("태안", R.drawable.cn3)
        ));
        DESTINATION_TO_PLACES.put("공주", Arrays.asList(
                new Place("공산성", R.drawable.jb1, "유적", 4.6f, "공주시 웅진로 280"),
                new Place("무령왕릉과 왕릉원", R.drawable.jb1, "유적", 4.7f, "공주시 왕릉로 37"),
                new Place("마곡사", R.drawable.jb1, "사찰", 4.5f, "공주시 사곡면 마곡사로 966"),
                new Place("공주 한옥마을", R.drawable.jb1, "명소", 4.2f, "공주시 관광단지길 12"),
                new Place("금강", R.drawable.jb1, "강변", 4.0f, "공주시 웅진동 일대")
        ));
        DESTINATION_TO_PLACES.put("부여", Arrays.asList(
                new Place("부여 능산리 고분군", R.drawable.jb1, "유적", 4.5f, "부여군 부여읍 왕릉로"),
                new Place("정림사지 5층 석탑", R.drawable.jb1, "유적", 4.6f, "부여군 부여읍 동남리"),
                new Place("궁남지", R.drawable.jb1, "명소", 4.7f, "부여군 부여읍 궁남로 52"),
                new Place("부소산성 낙화암", R.drawable.jb1, "유적", 4.4f, "부여군 부여읍 부소로 12"),
                new Place("국립부여박물관", R.drawable.jb1, "박물관", 4.3f, "부여군 부여읍 금성로 5")
        ));
        DESTINATION_TO_PLACES.put("태안", Arrays.asList(
                new Place("만리포 해수욕장", R.drawable.jb1, "해변", 4.3f, "태안군 소원면 만리포해수욕장길 167"),
                new Place("꽃지 해변", R.drawable.jb1, "해변", 4.6f, "태안군 안면읍 꽃지해안로 400"),
                new Place("신두리 해안사구", R.drawable.jb1, "자연", 4.5f, "태안군 원북면 신두해변길 201-10"),
                new Place("천리포수목원", R.drawable.jb1, "수목원", 4.7f, "태안군 소원면 천리포1길 187"),
                new Place("안면도", R.drawable.jb1, "섬", 4.4f, "태안군 안면읍")
        ));


        // ========== 충청북도 ==========
        REGION_TO_DESTINATIONS.put("충청북도", Arrays.asList(
                new TravelDestination("단양", R.drawable.cb1),
                new TravelDestination("청주", R.drawable.cb2),
                new TravelDestination("제천", R.drawable.cb3)
        ));
        DESTINATION_TO_PLACES.put("단양", Arrays.asList(
                new Place("도담삼봉", R.drawable.jb1, "자연", 4.7f, "단양군 매포읍 하괴리 84-1"),
                new Place("만천하스카이워크", R.drawable.jb1, "전망대", 4.6f, "단양군 적성면 애곡리 94"),
                new Place("고수동굴", R.drawable.jb1, "동굴", 4.3f, "단양군 단양읍 고수동굴길 8"),
                new Place("단양구경시장", R.drawable.jb1, "시장", 4.0f, "단양군 단양읍 도전4길 31"),
                new Place("패러글라이딩", R.drawable.jb1, "체험", 4.8f, "단양군 가곡면")
        ));
        DESTINATION_TO_PLACES.put("청주", Arrays.asList(
                new Place("청남대", R.drawable.jb1, "대통령별장", 4.7f, "청주시 상당구 문의면 청남대길 646"),
                new Place("상당산성", R.drawable.jb1, "성곽/역사", 4.4f, "청주시 상당구 산성동 산1"),
                new Place("수암골 벽화마을", R.drawable.jb1, "벽화마을", 4.2f, "청주시 상당구 수암로59번길"),
                new Place("오송역", R.drawable.jb1, "교통", 3.5f, "청주시 흥덕구 오송읍 오송가락로 1230"),
                new Place("청주국제공항", R.drawable.jb1, "교통", 3.7f, "청주시 청원구 내수읍 오창대로 988")
        ));
        DESTINATION_TO_PLACES.put("제천", Arrays.asList(
                new Place("의림지", R.drawable.jb1, "저수지/공원", 4.5f, "제천시 의림지로 213"),
                new Place("청풍호반 케이블카", R.drawable.jb1, "케이블카", 4.6f, "제천시 청풍면 청풍명월로 870"),
                new Place("박달재", R.drawable.jb1, "고개/명소", 4.0f, "제천시 백운면 박달로 872"),
                new Place("리솜포레스트", R.drawable.jb1, "리조트", 4.2f, "제천시 백운면 금봉로 365"),
                new Place("자연치유 도시", R.drawable.jb1, "관광", 3.8f, "제천시 전역")
        ));


        // ========== 전라남도 ==========
        REGION_TO_DESTINATIONS.put("전라남도", Arrays.asList(
                new TravelDestination("여수", R.drawable.jn1),
                new TravelDestination("순천", R.drawable.jn2),
                new TravelDestination("목포", R.drawable.jn3)
        ));
        DESTINATION_TO_PLACES.put("여수", Arrays.asList(
                new Place("여수 밤바다", R.drawable.jb1, "야경/해변", 4.7f, "여수시 종화동 일대"),
                new Place("오동도", R.drawable.jb1, "섬/공원", 4.5f, "여수시 오동도로 222"),
                new Place("해상 케이블카", R.drawable.jb1, "체험", 4.6f, "여수시 돌산읍 돌산로 3617-1"),
                new Place("돌산공원", R.drawable.jb1, "공원/전망대", 4.4f, "여수시 돌산읍 돌산로 3641-61"),
                new Place("낭만포차", R.drawable.jb1, "음식/야경", 4.3f, "여수시 하멜로 102")
        ));
        DESTINATION_TO_PLACES.put("순천", Arrays.asList(
                new Place("순천만 국가정원", R.drawable.jb1, "정원", 4.8f, "순천시 국가정원1호길 47"),
                new Place("순천만 습지", R.drawable.jb1, "자연", 4.7f, "순천시 순천만길 513-25"),
                new Place("낙안읍성", R.drawable.jb1, "역사/문화", 4.5f, "순천시 낙안면 충민길 30"),
                new Place("드라마 세트장", R.drawable.jb1, "촬영지", 4.2f, "순천시 비례골길 24"),
                new Place("선암사", R.drawable.jb1, "사찰", 4.3f, "순천시 승주읍 선암사길 450")
        ));
        DESTINATION_TO_PLACES.put("목포", Arrays.asList(
                new Place("목포 해상케이블카", R.drawable.jb1, "케이블카", 4.6f, "목포시 해양대학로 240"),
                new Place("갓바위", R.drawable.jb1, "자연", 4.3f, "목포시 남항로155번길 20"),
                new Place("목포 근대역사관", R.drawable.jb1, "박물관", 4.4f, "목포시 영산로29번길 1"),
                new Place("유달산", R.drawable.jb1, "산/공원", 4.5f, "목포시 유달로 180"),
                new Place("연희네 슈퍼", R.drawable.jb1, "촬영지/명소", 4.0f, "목포시 해안로 163번길 13-2")
        ));


        // ========== 경기도 ==========
        REGION_TO_DESTINATIONS.put("경기도", Arrays.asList(
                new TravelDestination("수원", R.drawable.gg1),
                new TravelDestination("가평", R.drawable.gg2),
                new TravelDestination("용인", R.drawable.gg3)
        ));
        DESTINATION_TO_PLACES.put("수원", Arrays.asList(
                new Place("수원화성", R.drawable.jb1, "유적", 4.8f, "수원시 팔달구 정조로 825"),
                new Place("화성행궁", R.drawable.jb1, "유적", 4.7f, "수원시 팔달구 정조로 825"),
                new Place("행리단길", R.drawable.jb1, "거리/상점가", 4.4f, "수원시 팔달구 신풍동"),
                new Place("지동시장", R.drawable.jb1, "시장", 4.1f, "수원시 팔달구 팔달문로 3번길 20"),
                new Place("광교호수공원", R.drawable.jb1, "공원", 4.5f, "수원시 영통구 광교호수로 165")
        ));
        DESTINATION_TO_PLACES.put("가평", Arrays.asList(
                new Place("남이섬", R.drawable.jb1, "섬/명소", 4.7f, "가평군 가평읍 북한강변로 1024"),
                new Place("쁘띠프랑스", R.drawable.jb1, "테마파크", 4.2f, "가평군 청평면 호반로 1063"),
                new Place("아침고요수목원", R.drawable.jb1, "수목원", 4.6f, "가평군 상면 수목원로 432"),
                new Place("제이드가든", R.drawable.jb1, "수목원", 4.4f, "춘천시 남산면 서천리 산111"),
                new Place("캠핑", R.drawable.jb1, "체험", 4.0f, "가평군 일대")
        ));
        DESTINATION_TO_PLACES.put("용인", Arrays.asList(
                new Place("에버랜드", R.drawable.jb1, "테마파크", 4.8f, "용인시 처인구 포곡읍 에버랜드로 199"),
                new Place("한국민속촌", R.drawable.jb1, "역사/문화", 4.7f, "용인시 기흥구 민속촌로 90"),
                new Place("캐리비안 베이", R.drawable.jb1, "워터파크", 4.5f, "용인시 처인구 포곡읍 에버랜드로 199"),
                new Place("용인 자연휴양림", R.drawable.jb1, "자연휴양림", 4.3f, "용인시 처인구 모현읍 초부로 220"),
                new Place("백남준 아트센터", R.drawable.jb1, "미술관", 4.0f, "용인시 기흥구 백남준로 10")
        ));


        // ========== 강원도 ==========
        REGION_TO_DESTINATIONS.put("강원도", Arrays.asList(
                new TravelDestination("속초", R.drawable.gy1),
                new TravelDestination("강릉", R.drawable.gy2),
                new TravelDestination("춘천", R.drawable.gy3)
        ));
        DESTINATION_TO_PLACES.put("속초", Arrays.asList(
                new Place("속초 해수욕장", R.drawable.jb1, "해변", 4.5f, "속초시 해오름로 186"),
                new Place("설악산 국립공원", R.drawable.jb1, "국립공원", 4.9f, "속초시 설악동"),
                new Place("아바이마을", R.drawable.jb1, "음식/마을", 4.2f, "속초시 청호동"),
                new Place("속초 중앙시장", R.drawable.jb1, "시장", 4.3f, "속초시 중앙로 147번길 16"),
                new Place("영금정", R.drawable.jb1, "정자/일출", 4.1f, "속초시 영금정로 43")
        ));
        DESTINATION_TO_PLACES.put("강릉", Arrays.asList(
                new Place("강릉 커피거리", R.drawable.jb1, "카페거리", 4.6f, "강릉시 창해로14번길 일대"),
                new Place("정동진", R.drawable.jb1, "해변/일출", 4.7f, "강릉시 강동면 정동진리"),
                new Place("오죽헌", R.drawable.jb1, "유적", 4.5f, "강릉시 율곡로3139번길 24"),
                new Place("안목 해변", R.drawable.jb1, "해변", 4.4f, "강릉시 창해로14번길"),
                new Place("경포대", R.drawable.jb1, "호수/누각", 4.3f, "강릉시 경포로 365")
        ));
        DESTINATION_TO_PLACES.put("춘천", Arrays.asList(
                new Place("남이섬", R.drawable.jb1, "섬/명소", 4.7f, "춘천시 남산면 남이섬길 1"),
                new Place("소양강 스카이워크", R.drawable.jb1, "스카이워크", 4.5f, "춘천시 영서로 2663"),
                new Place("김유정 문학촌", R.drawable.jb1, "문학관", 4.3f, "춘천시 신동면 실레길 25"),
                new Place("제이드가든", R.drawable.jb1, "수목원", 4.4f, "춘천시 남산면 서천리 산111"),
                new Place("닭갈비 골목", R.drawable.jb1, "음식", 4.2f, "춘천시 조양동")
        ));


        // 기본값 (region이 null이거나 매칭되지 않을 경우)
        REGION_TO_DESTINATIONS.put("default", Arrays.asList(
                new TravelDestination("제주", R.drawable.jb1) // 기본 여행지 이미지도 jb1로 변경
        ));
        DESTINATION_TO_PLACES.put("제주", Arrays.asList(
                new Place("성산일출봉", R.drawable.jb1, "자연", 4.8f, "제주시 성산읍 성산리 1번지"),
                new Place("한라산", R.drawable.jb1, "자연", 4.9f, "제주시 한라산국립공원"),
                new Place("천지연폭포", R.drawable.jb1, "자연", 4.6f, "서귀포시 천지연로 663"),
                new Place("동문시장", R.drawable.jb1, "시장/음식", 4.3f, "제주시 관덕로14길 20")
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
        public int imageResId;
        public String category;
        public float rating;
        public String address;
        public boolean isChecked;

        public Place(String name, int imageResId, String category, float rating, String address) {
            this.name = name;
            this.imageResId = imageResId;
            this.category = category;
            this.rating = rating;
            this.address = address;
            this.isChecked = false;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public boolean isChecked() {
            return isChecked;
        }
    }

    public static List<TravelDestination> getDestinationsForRegion(String region) {
        return REGION_TO_DESTINATIONS.getOrDefault(region, REGION_TO_DESTINATIONS.get("default"));
    }

    public static List<Place> getPlacesForDestination(String destinationName) {
        return DESTINATION_TO_PLACES.getOrDefault(destinationName, Collections.emptyList());
    }
}