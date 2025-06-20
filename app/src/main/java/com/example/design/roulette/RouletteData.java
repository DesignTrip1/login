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
                new TravelDestination("전주", R.drawable.korea_jb_jeonju),
                new TravelDestination("남원", R.drawable.korea_jb_namwon),
                new TravelDestination("정읍", R.drawable.korea_jb_jeongeup)
        ));
        DESTINATION_TO_PLACES.put("전주", Arrays.asList(
                new Place("전주 한옥마을", R.drawable.korea_jb_jeonju_deokjin, "명소", 4.4f, "전주시 완산구 태조로 44", 35.8175376f, 127.1520417f, "ChIJS73uEmIjcDURjoTQxhu-9I4", "user_default", "group_default"),
                new Place("전주 덕진공원", R.drawable.korea_jb_jeonju_deokjin, "공원/자연", 4.1f, "전주시 덕진구 권삼득로 390", 35.84757319999999f, 127.121895f, "ChIJw5VFM8o8cDUR72L1T-NDeOs", "user_default", "group_default"),
                new Place("전주 경기전", R.drawable.korea_jb_jeonju_gyeonggijeon, "유적", 4.4f, "전주시 완산구 풍남동 경기전길 1", 35.8153492f, 127.1497938f, "ChIJ5WryHGAjcDURMUsEZ8rflR8", "user_default", "group_default"),
                new Place("현대옥 전주본점", R.drawable.korea_jb_jeonju_hyeondaeok, "음식", 4.3f, "전주시 완산구 팔달로 149", 35.8197406f, 127.1171446f, "ChIJSwE_P1A7cDUR61oVx-prd4o", "user_default", "group_default"),
                new Place("베테랑", R.drawable.korea_jb_jeonju_veteran, "음식", 4.3f, "전주시 완산구 경기전길 135", 35.8134449f, 127.1514042f, "ChIJCR9bZ54kcDURI9We4ug6Ku8", "user_default", "group_default"),
                new Place("길거리야", R.drawable.korea_jb_jeonju_gilgeoriya, "음식", 4.0f, "전주시 완산구 경기전길 124", 35.8139813f, 127.1511079f, "ChIJIaSlQ54kcDUR3UFOERqYPCc", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("남원", Arrays.asList(
                new Place("광한루원", R.drawable.korea_jb_namwon_gwanghalluwon, "명소", 4.5f, "남원시 요천로 1447", 35.4032881f, 127.3794357f, "ChIJQ42pCTIpbjURMzkDDxsymb0", "user_default", "group_default"),
                new Place("남원시립김병종미술관", R.drawable.korea_jb_namwon_artmuseum, "미술관", 4.7f, "남원시 어현동 340", 35.3988097f, 127.3895446f, "ChIJD3Ms3dEubjURHUVMACzAKes", "user_default", "group_default"),
                new Place("춘향테마파크", R.drawable.korea_jb_namwon_chunhyang, "테마파크", 4.2f, "남원시 양림길 14-9", 35.4016267f, 127.3867338f, "ChIJB0x_zc0ubjURk13PJabme0k", "user_default", "group_default"),
                new Place("서남만찬", R.drawable.korea_jb_namwon_seonammanchan, "음식", 4.3f, "남원시 하정동 146-2", 35.41828150000001f, 127.3980922f, "ChIJJwgwcKQubjURw9YJfVPhQME", "user_default", "group_default"),
                new Place("그랑깨", R.drawable.korea_jb_namwon_grandkkae, "음식", 4.0f, "남원시 대산면 대곡리 347", 35.4058198f, 127.3782444f, "ChIJr-8sMzIpbjURDEB7tOqO_xs", "user_default", "group_default"),
                new Place("현식당", R.drawable.korea_jb_namwon_hyeonsikdang, "음식", 4.2f, "남원시 향교동 548", 35.4013293f, 127.3770065f, "ChIJfwjIhi4pbjURhZxu0oDJH38", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("정읍", Arrays.asList(
                new Place("내장산국립공원", R.drawable.korea_jb_jeongeup_naejangsan, "국립공원", 4.7f, "정읍시 내장산로 936", 35.4810895f, 126.8893736f, "ChIJnebHtWy5cTURSPOlGKMPft0", "user_default", "group_default"),
                new Place("무성서원", R.drawable.korea_jb_jeongeup_museongseowon, "유적", 4.6f, "정읍시 칠보면 무성리 110", 35.6018552f, 126.9838546f, "ChIJFbRk2VrKcTURGMtKa9SzPx0", "user_default", "group_default"),
                new Place("정읍쌍화차거리", R.drawable.korea_jb_jeongeup_ssanghwacha, "거리", 4.2f, "정읍시 명륜1길 16-16", 35.5670483f, 126.8571199f, "ChIJxZL0bdexcTURxIRWyB9VeNU", "user_default", "group_default"),
                new Place("정읍 오브제", R.drawable.korea_jb_jeongeup_objet, "카페", 4.1f, "정읍시 수성동 637-2", 35.5565763f, 127.0242274f, "ChIJG832tBLJcTURpKKYL-APsWE", "user_default", "group_default"),
                new Place("양자강", R.drawable.korea_jb_jeongeup_yangjagang, "음식", 4.0f, "정읍시 중앙1길 35", 35.5666438f, 126.8573752f, "ChIJVebrV8-wcTUR-I8CEIoC9Jo", "user_default", "group_default"),
                new Place("국화회관", R.drawable.korea_jb_jeongeup_gukhwahoegwan, "음식", 3.9f, "정읍시 수성동 686-2", 35.5701907f, 126.8454247f, "ChIJ_br-HuqwcTURfCsynRSBF_0", "user_default", "group_default")
        ));


        // ========== 제주도 ==========
        REGION_TO_DESTINATIONS.put("제주도", Arrays.asList(
                new TravelDestination("제주시", R.drawable.korea_jj_jejusi),
                new TravelDestination("애월읍", R.drawable.korea_jj_aewol),
                new TravelDestination("서귀포시", R.drawable.korea_jj_seogwipo)
        ));
        DESTINATION_TO_PLACES.put("제주시", Arrays.asList(
                new Place("삼성혈", R.drawable.korea_jj_jejusi_samsunghyeol, "유적", 4.3f, "제주특별자치도 제주시 삼성로 22", 33.5047027f, 126.529358f, "ChIJh73xcqz8DDUR1KDWUX7I1_4", "user_default", "group_default"),
                new Place("용담계곡", R.drawable.korea_jj_jejusi_yongyeon, "자연", 4.2f, "제주 제주시 용담1동 2581-4", 33.5120363f, 126.5141684f, "ChIJFVn1msD7DDURKGhqHuIJ6o8", "user_default", "group_default"),
                new Place("이호테우 해변", R.drawable.korea_jj_jejusi_ihotaewu, "해변", 4.2f, "제주 제주시 이호일동", 33.49795710000001f, 126.4531502f, "ChIJR4ms24v6DDURR__8pKQad3w", "user_default", "group_default"),
                new Place("도두봉", R.drawable.korea_jj_jejusi_dodubong, "명소", 4.1f, "제주특별자치도 제주시 도두항길 4-17", 33.508095f, 126.468593f, "ChIJBXJICej6DDURGZqQDPB3YUU", "user_default", "group_default"),
                new Place("우진해장국", R.drawable.korea_jj_jejusi_woojin, "음식", 4.4f, "제주시 서사로 11", 33.511505f, 126.5200319f, "ChIJ1RG1J6vkDDURux7pPbxUvbY", "user_default", "group_default"),
                new Place("아베베베이커리 제주점", R.drawable.korea_jj_jejusi_abebebakery, "음식", 4.5f, "제주 제주시 동문로6길 4 1-3층(일도일동)", 33.51265759999999f, 126.5287951f, "ChIJaz9NT0PjDDUR2CIr9m4ndj8", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("애월읍", Arrays.asList(
                new Place("새별오름", R.drawable.korea_jj_aewol_saebyeol, "자연", 4.5f, "제주 제주시 애월읍 봉성리 산59-8", 33.3659432f, 126.35644f, "ChIJISPJvy5ZDDUR9-piabjbc7E", "user_default", "group_default"),
                new Place("수산봉", R.drawable.korea_jj_aewol_susanbong, "자연", 4.3f, "제주 제주시 애월읍 수산리 산1-1", 33.473855f, 126.3841261f, "ChIJs_hE8pL3DDURc1ujlWyUj58", "user_default", "group_default"),
                new Place("곽지해수욕장", R.drawable.korea_jj_aewol_gwakji, "해변", 4.3f, "제주 제주시 애월읍 곽지리", 33.450902f, 126.3057298f, "ChIJwxapcG71DDURGdhIIeiFBcI", "user_default", "group_default"),
                new Place("상가리야자숲", R.drawable.korea_jj_aewol_sangari, "자연", 4.2f, "제주 제주시 애월읍 고하상로 326", 33.4391766f, 126.3583243f, "ChIJi8JGEEj3DDURfyqH52eX1oM", "user_default", "group_default"),
                new Place("금돈가", R.drawable.korea_jj_aewol_geumdonga, "음식", 4.3f, "제주특별자치도 제주시 애월읍 가문동남길 63 1층", 33.4809643f, 126.3931794f, "ChIJxYVnty3xDDURYczCEuxWJZQ", "user_default", "group_default"),
                new Place("언덕집국수", R.drawable.korea_jj_aewol_eondeokjib, "음식", 4.1f, "제주특별자치도 제주시 애월읍 애월해안로 494 1층", 33.4784053f, 126.3569628f, "ChIJq_Zg5lr3DDUR_SEbLunL4VU", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("서귀포시", Arrays.asList(
                new Place("쇠소깍", R.drawable.korea_jj_seogwipo_soesokkak, "자연", 4.5f, "제주특별자치도 서귀포시 쇠소깍로 104", 33.2524694f, 126.6235333f, "ChIJDYjcLFurDTUR-fF9Te-Wt7w", "user_default", "group_default"),
                new Place("정방폭포", R.drawable.korea_jj_seogwipo_jeongbang, "자연", 4.4f, "제주 서귀포시 칠십리로214번길 37", 33.2448521f, 126.5718032f, "ChIJ8afI-XNTDDURC7OZzyBO8KA", "user_default", "group_default"),
                new Place("보목포구", R.drawable.korea_jj_seogwipo_bomokpogu, "항구", 4.1f, "제주특별자치도 서귀포시 보목포로 46", 33.23953170000001f, 126.6081687f, "ChIJiYixZlJTDDURDVdf8tdq1ek", "user_default", "group_default"),
                new Place("원앙폭포", R.drawable.korea_jj_seogwipo_wonang, "자연", 4.0f, "제주 서귀포시 돈내코로 137", 33.3002515f, 126.5801543f, "ChIJhcTEZBxVDDUR8r2GtHLP9PA", "user_default", "group_default"),
                new Place("미영이네 본점", R.drawable.korea_jj_seogwipo_miyeong, "음식", 4.4f, "제주 서귀포시 대정읍 하모항구로 42", 33.2177388f, 126.2498932f, "ChIJAzVlLPdpDDURwtrd3yMUWnM", "user_default", "group_default"),
                new Place("아줄레주", R.drawable.korea_jj_seogwipo_azulejo, "카페", 4.1f, "제주특별자치도 서귀포시 성산읍 신풍하동로19번길 59", 33.366762f, 126.8391336f, "ChIJh-bdcVkODTURf6SB2E90_No", "user_default", "group_default")
        ));


        // ========== 경상북도 ==========
        REGION_TO_DESTINATIONS.put("경상북도", Arrays.asList(
                new TravelDestination("경산", R.drawable.korea_gb_gyeongsan),
                new TravelDestination("구미", R.drawable.korea_gb_gumi),
                new TravelDestination("칠곡", R.drawable.korea_gb_chilgok)
        ));
        DESTINATION_TO_PLACES.put("경산", Arrays.asList(
                new Place("경산 자연마당", R.drawable.korea_gb_gyeongsan_nature, "공원", 4.3f, "경북 경산시 중방동 683-1", 35.826712f, 128.7419601f, "ChIJC2jwZS0NZjURwyb9k0F_UHg", "user_default", "group_default"),
                new Place("다솜생태자연학습관", R.drawable.korea_gb_gyeongsan_dasom, "교육", 4.0f, "경북 경산시 중앙로 39 NC경산점 5층", 35.8509866f, 128.6173228f, "ChIJV705akniZTURkyqGQ_0u_6M", "user_default", "group_default"),
                new Place("이웃집수달", R.drawable.korea_gb_gyeongsan_otter, "카페", 4.1f, "경북 경산시 한의대로 136 이웃집수달", 35.8088403f, 128.777577f, "ChIJVZqeoFbjZTURrRBZA-6LH2M", "user_default", "group_default"),
                new Place("시간의 가치 경산점", R.drawable.korea_gb_gyeongsan_timevalue, "카페", 4.1f, "경북 경산시 경산로 136-1 단층) 시간의가치 가죽공예 작업실", 35.8208806f, 128.7288414f, "ChIJRQ6xZwAJZjUROu5ZN_OEKpE", "user_default", "group_default"),
                new Place("테이스티가든", R.drawable.korea_gb_gyeongsan_tastygarden, "음식", 4.0f, "경북 경산시 삼성현로 548 1층", 35.8046631f, 128.7590813f, "ChIJnznvDQAPZjURGixDOFgcQVQ", "user_default", "group_default"),
                new Place("스페이스임원", R.drawable.korea_gb_gyeongsan_spaceimwon, "명소", 3.8f, "경산시 와촌면 강학리 617", 35.90057960000001f, 128.8098603f, "ChIJiX6N_3VzZjURHqNpwrES4dY", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("구미", Arrays.asList(
                new Place("금리단길", R.drawable.korea_gb_gumi_geumridangil, "거리/상점가", 4.0f, "경상북도 구미시 원남로8길 13", 36.1261646f, 128.3309464f, "ChIJtS20FgDBZTUROgdzFbFIPPM", "user_default", "group_default"),
                new Place("금오랜드", R.drawable.korea_gb_gumi_geumo, "테마파크", 3.8f, "경상북도 구미시 금오산로 341", 36.1131025f, 128.3161266f, "ChIJT8j97WG_ZTURygUc4p0GsgQ", "user_default", "group_default"),
                new Place("물고기나라", R.drawable.korea_gb_gumi_fishland, "아쿠아리움", 4.0f, "경북 구미시 지산동 845-353 물고기나라", 36.1369908f, 128.3531873f, "ChIJh55B2SPHZTURQS8SPtrPC88", "user_default", "group_default"),
                new Place("김태주선산곱창 구미본점", R.drawable.korea_gb_gumi_gobchang, "음식", 4.3f, "경북 구미시 송원서로 77", 36.12464600000001f, 128.3432722f, "ChIJO2XgpczAZTURCIfQc2JRTF4", "user_default", "group_default"),
                new Place("모에누베이커리", R.drawable.korea_gb_gumi_moenou, "음식", 4.1f, "경북 구미시 고아읍 봉한3길 4 모에누 베이커리", 36.1817657f, 128.350103f, "ChIJsTZc98DHZTUREwRHQEw06eA", "user_default", "group_default"),
                new Place("가가스시", R.drawable.korea_gb_gumi_gagasushi, "음식", 4.3f, "경북 구미시 상사서로 104 1층 102호", 36.09170290000001f, 128.3556109f, "ChIJV110SafBZTUR05IMWUkRU9U", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("칠곡", Arrays.asList(
                new Place("칠곡보 사계절 썰매장", R.drawable.korea_gb_chilgok_sled, "체험", 4.0f, "경북 칠곡군 석적읍 강변대로 1592 칠곡보사계절썰매장", 36.0188441f, 128.4059766f, "ChIJIeufAILpZTURfTrcTLGDP_I", "user_default", "group_default"),
                new Place("꿀벌나라 테마공원", R.drawable.korea_gb_chilgok_bees, "테마공원", 4.1f, "경북 칠곡군 석적읍 강변대로 1580-1", 36.0167376f, 128.4048334f, "ChIJwwFNmcjpZTURN566wcT_Btw", "user_default", "group_default"),
                new Place("호국의 다리", R.drawable.korea_gb_chilgok_bridge, "명소", 4.3f, "경북 칠곡군 왜관읍 석전리", 36.000658f, 128.391644f, "ChIJ-W7QGjPrZTURcmSm6PrXS74", "user_default", "group_default"),
                new Place("시호재", R.drawable.korea_gb_chilgok_sihojae, "카페", 4.1f, "경북 칠곡군 석적읍 망정1길 11-21 시호재", 36.040643f, 128.442342f, "ChIJscG0YGfDZTURZu8hm8aoFqY", "user_default", "group_default"),
                new Place("황금원", R.drawable.korea_gb_chilgok_hwanggeumwon, "음식", 4.0f, "경북 칠곡군 약목면 관호8길 16", 36.0021589f, 128.3890698f, "ChIJ_____zDqZTUR7t_J0FL-BMQ", "user_default", "group_default"),
                new Place("버터앤브루", R.drawable.korea_gb_chilgok_butterbrew, "음식", 3.9f, "경북 칠곡군 동명면 기성3길 5 카페 버터앤브루", 36.0039333f, 128.595168f, "ChIJ59T1HwDfZTURVYeoSrctYGM", "user_default", "group_default")
        ));


        // ========== 경상남도 ==========
        REGION_TO_DESTINATIONS.put("경상남도", Arrays.asList(
                new TravelDestination("부산", R.drawable.korea_gn_busan),
                new TravelDestination("사천", R.drawable.korea_gn_sachun),
                new TravelDestination("진주", R.drawable.korea_gn_jinju)
        ));
        DESTINATION_TO_PLACES.put("부산", Arrays.asList(
                new Place("롯데월드 어드벤처", R.drawable.korea_gn_busan_lotteworld, "테마파크", 4.4f, "부산광역시 기장군 기장읍 동부산관광로 42", 35.1962453f, 129.2149345f, "ChIJ8XYSxfKNaDURFuMe6s2K4us", "user_default", "group_default"),
                new Place("광안리 해수욕장", R.drawable.korea_gn_busan_gwangalli, "해변", 4.5f, "부산 수영구 광안해변로 219", 35.1531696f, 129.118666f, "ChIJxw7HJy_taDUR-xaSTeHwbf8", "user_default", "group_default"),
                new Place("해운대 해수욕장", R.drawable.korea_gn_busan_haeundae, "해변", 4.5f, "부산 해운대구 우동", 35.1586975f, 129.1603842f, "ChIJXwf-DlyNaDURmKxjwdWxY5k", "user_default", "group_default"),
                new Place("이재모피자 본점", R.drawable.korea_gn_busan_pizzabunjeom, "음식", 4.4f, "부산 부산진구 전포대로209번길 21", 35.1021046f, 129.0305985f, "ChIJ8VYk1e74IBURIIVPcKTynRI", "user_default", "group_default"),
                new Place("톤쇼우 광안점", R.drawable.korea_gn_busan_tonshow, "음식", 4.4f, "부산 수영구 광안해변로279번길 13 1층", 35.1564409f, 129.1248005f, "ChIJZ74eNBLtaDUR-IqtSdDWWK4", "user_default", "group_default"),
                new Place("신발원", R.drawable.korea_gn_busan_sinbalwon, "음식", 4.3f, "부산 동구 대영로243번길 62", 35.1148152f, 129.038688f, "ChIJXYniPn3paDURJqjdCZneaAo", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("사천", Arrays.asList(
                new Place("사천 바다 케이블카", R.drawable.korea_gn_sachun_cablecar, "케이블카", 4.5f, "경남 사천시 사천대로 18", 34.93367810000001f, 128.0531511f, "ChIJmfjPiRiLbjURvl383JpaFYY", "user_default", "group_default"),
                new Place("아라마루 아쿠아리움 본관", R.drawable.korea_gn_sachun_aquarium, "아쿠아리움", 4.2f, "경남 사천시 사천대로 18", 34.9261962f, 128.0450726f, "ChIJTSFAGkaLbjURCiVz6_CJVo8", "user_default", "group_default"),
                new Place("항공우주박물관", R.drawable.korea_gn_sachun_aerospace, "박물관", 4.1f, "경남 사천시 사남면 공단1로 78 항공우주박물관", 35.07137209999999f, 128.0624681f, "ChIJY0iV5ir3bjURQGDvo6rNr0U", "user_default", "group_default"),
                new Place("신일밀면", R.drawable.korea_gn_sachun_milmyeon, "음식", 4.1f, "경남 사천시 사천읍 동문로 49 신일밀면", 35.0825055f, 128.0967908f, "ChIJnWuSso_wbjURVzXx_y7Jnyg", "user_default", "group_default"),
                new Place("Burger FLUFFY", R.drawable.korea_gn_sachun_burgerfluffy, "음식", 3.9f, "경남 사천시 미룡길 28 버거플러피", 34.9878771f, 128.0541857f, "ChIJLSn3_qr1bjURDCZxf5K-f4U", "user_default", "group_default"),
                new Place("삼천포맛집정서방", R.drawable.korea_gn_sachun_samcheonpo, "음식", 4.0f, "경남 사천시 진삼로 269 삼천포맛집정서방", 34.9805262f, 128.0625184f, "ChIJhwKJ7Z30bjURpnUUKLQP_Rw", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("진주", Arrays.asList(
                new Place("진주성", R.drawable.korea_gn_jinju_jinju_seong, "유적", 4.7f, "경남 진주시 본성동", 35.18902440000001f, 128.078001f, "ChIJu7jQLor8bjURqG7qLhh143w", "user_default", "group_default"),
                new Place("경상남도 수목원", R.drawable.korea_gn_jinju_arboretum, "수목원", 4.4f, "경남 진주시 이반성면 수목원로 386 경상남도수목원", 35.1606488f, 128.2954266f, "ChIJ7Zcanz3gbjURL96BW9b1Yoc", "user_default", "group_default"),
                new Place("촉석루", R.drawable.korea_gn_jinju_chokseokru, "누각", 4.5f, "경남 진주시 남강로 626", 35.1894393f, 128.0818561f, "ChIJP4mkiBT8bjUR79EmGwt8mMk", "user_default", "group_default"),
                new Place("하연옥 본점", R.drawable.korea_gn_jinju_hayeonok, "음식", 4.4f, "경남 진주시 진주대로 1317-20", 35.1939301f, 128.0607229f, "ChIJb3_ymUv_bjUR4Bazfq5d-Ag", "user_default", "group_default"),
                new Place("수복빵집", R.drawable.korea_gn_jinju_subokbakery, "음식", 4.3f, "경남 진주시 진주대로1088번길 8 1층 수복빵집", 35.1966287f, 128.0837755f, "ChIJA9cBZz_8bjURn-f4GlE8ojM", "user_default", "group_default"),
                new Place("천황식당", R.drawable.korea_gn_jinju_cheonhwang, "음식", 4.2f, "경남 진주시 촉석로207번길 3", 35.1961169f, 128.0845091f, "ChIJvQmtnUD8bjURJRZJefhgtGs", "user_default", "group_default")
        ));


        // ========== 충청남도 ==========
        REGION_TO_DESTINATIONS.put("충청남도", Arrays.asList(
                new TravelDestination("아산", R.drawable.korea_cn_asan),
                new TravelDestination("태안", R.drawable.korea_cn_taean),
                new TravelDestination("공주", R.drawable.korea_cn_gongju)
        ));
        DESTINATION_TO_PLACES.put("아산", Arrays.asList(
                new Place("현충사", R.drawable.korea_cn_asan_hyeonchungsa, "유적", 4.6f, "충남 아산시 염치읍 현충사길 126", 36.80812710000001f, 127.0332832f, "ChIJgybG3IXfejURkU9AXTO1mSc", "user_default", "group_default"),
                new Place("피나클랜드 수목원", R.drawable.korea_cn_asan_pinnacleland, "정원", 4.3f, "충남 아산시 영인면 월선길 20-42", 36.8749551f, 126.9280855f, "ChIJwyPQdegfezURqZmQVB3Qf1A", "user_default", "group_default"),
                new Place("오엠엠", R.drawable.korea_cn_asan_omm, "카페", 4.1f, "충남 아산시 방축로53번길 26 오엠엠", 36.7832351f, 126.9904523f, "ChIJq2kmFgDfejURRCOgN2Zkty0", "user_default", "group_default"),
                new Place("당림미술관", R.drawable.korea_cn_asan_danglim, "미술관", 4.0f, "충남 아산시 송악면 외암로1182번길 34-19 당림미술관", 36.742747f, 127.0186944f, "ChIJBd3EmGrcejURWa6XKSCmuEI", "user_default", "group_default"),
                new Place("목화반점", R.drawable.korea_cn_asan_mokhwa, "음식", 3.9f, "충남 아산시 온주길 28-8", 36.761324f, 127.0193104f, "ChIJv5UMjh_cejURS52Nf6eoCYw", "user_default", "group_default"),
                new Place("아레피", R.drawable.korea_cn_asan_arepi, "음식", 3.8f, "충남 아산시 영인면 영인로 187-15 ALEFFEE (아레피)", 36.8639934f, 126.9478949f, "ChIJfXcADPEfezURErxT70YG328", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("태안", Arrays.asList(
                new Place("코리아플라워파크", R.drawable.korea_cn_taean_flowerpark, "테마파크", 4.3f, "충남 태안군 안면읍 꽃지해안로 400 코리아플라워파크", 36.5006657f, 126.3368253f, "ChIJm3-RHvpyejURR0Kz0J9nJ90", "user_default", "group_default"),
                new Place("천리포수목원", R.drawable.korea_cn_taean_cheonripo, "수목원", 4.5f, "충남 태안군 소원면 천리포1길 187 천리포수목원", 36.7983617f, 126.1499174f, "ChIJOb6VMewWejURQko4_kBBxu0", "user_default", "group_default"),
                new Place("청산수목원", R.drawable.korea_cn_taean_cheongsang, "수목원", 4.1f, "충남 태안군 남면 연꽃길 70 충남 태안군 남면 신장리 18번지", 36.6882641f, 126.2970322f, "ChIJBVKXhfxsejURacZLi6LcVTU", "user_default", "group_default"),
                new Place("미식가", R.drawable.korea_cn_taean_misikga, "음식", 4.0f, "충남 태안군 태안읍 정주내3길 16-5 미식가", 36.74612860000001f, 126.3050276f, "ChIJedYcRgRrejURE5EgFoSMSmE", "user_default", "group_default"),
                new Place("커피인터뷰 파도리점", R.drawable.korea_cn_taean_coffeeinterview, "카페", 4.2f, "충남 태안군 소원면 파도길 61-9" ,36.737847f, 126.1331139f, "ChIJH8wnKu8RejURMPtbv6CPvkg", "user_default", "group_default"),
                new Place("딴뚝통나무집식당", R.drawable.korea_cn_taean_ddanttuk, "음식", 4.1f, "충남 태안군 안면읍 조운막터길 23-22", 36.50939290000001f, 126.3509956f, "ChIJkfqBtnh2ejURCPfgmV_Z5bc", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("공주", Arrays.asList(
                new Place("충청남도 역사박물관", R.drawable.korea_cn_gongju_mireuseom, "박물관", 4.2f, "충남 공주시 국고개길 24 충청남도역사박물관", 36.4536898f, 127.1271662f, "ChIJKddrP323ejUR_wtONAkm_60", "user_default", "group_default"),
                new Place("공산성", R.drawable.korea_cn_gongju_gongsanseong, "유적", 4.5f, "충남 공주시 금성동 53-51", 36.4646455f, 127.1249122f, "ChIJJ5tjA3-3ejUR-AmQcead1_A", "user_default", "group_default"),
                new Place("공주 경비행기", R.drawable.korea_cn_gongju_aircraft, "체험", 4.0f, "충남 공주시 의당면 수촌리 943", 36.50992889999999f, 127.1266289f, "ChIJKxbcDwjIejUR0ojsKhWvfI4", "user_default", "group_default"),
                new Place("미르섬", R.drawable.korea_cn_gongju_mireuseom, "섬/공원", 4.1f, "충남 공주시 금벽로 368",36.4674453f, 127.1284707f, "ChIJx40BvIe3ejURrW_gEyyn_ys", "user_default", "group_default"),
                new Place("베이커리밤마을", R.drawable.korea_cn_gongju_bakery, "음식", 4.4f, "충남 공주시 백미고을길 5-9 베이커리밤마을", 36.4647447f, 127.1229961f, "ChIJFbc4Jyu4ejURr4SpOP8eK5w", "user_default", "group_default"),
                new Place("곰골식당", R.drawable.korea_cn_gongju_gomgol, "음식", 4.1f, "충남 공주시 봉황산1길 1-2 곰골식당", 36.4517716f, 127.1206405f, "ChIJkRqwWNm5ejURWEh9DyWcBNs", "user_default", "group_default")
        ));


        // ========== 충청북도 ==========
        REGION_TO_DESTINATIONS.put("충청북도", Arrays.asList(
                new TravelDestination("단양", R.drawable.korea_cb_danyang),
                new TravelDestination("충주", R.drawable.korea_cb_chungju),
                new TravelDestination("제천", R.drawable.korea_cb_jecheon)
        ));
        DESTINATION_TO_PLACES.put("단양", Arrays.asList(
                new Place("소금정공원", R.drawable.korea_cb_danyang_sogeumjeong, "공원", 4.2f, "충북 단양군 단양읍 삼봉로 192", 36.9749449f, 128.3639692f, "ChIJN7CPK5f1YzUReh2irYJPsYM", "user_default", "group_default"),
                new Place("단양강 잔도", R.drawable.korea_cb_danyang_jando, "명소", 4.5f, "충북 단양군 적성면 애곡리 산18-15", 36.9780075f, 128.3396176f, "ChIJ7SgIS-H0YzURrCUC8e-5f1Q", "user_default", "group_default"),
                new Place("고수동굴", R.drawable.korea_cb_danyang_gosoo, "자연", 4.0f, "충북 단양군 단양읍 고수동굴길 8 고수동굴", 36.9884887f, 128.3815923f, "ChIJU01inBL0YzURDZHdPYSkIBw", "user_default", "group_default"),
                new Place("도담상봉", R.drawable.korea_cb_danyang_dodamsambong, "자연", 4.6f, "충북 단양군 매포읍 삼봉로 644", 37.0000443f, 128.3439702f, "ChIJ1_YpO5H0YzURW2e6XPn6qiA", "user_default", "group_default"),
                new Place("카페 산", R.drawable.korea_cb_danyang_cafe_san, "카페", 4.4f, "충북 단양군 가곡면 두산길 196-86", 36.99419049999999f, 128.3948028f, "ChIJefx9wvjzYzURPhWwUsHXWvQ", "user_default", "group_default"),
                new Place("단양흑마늘누룽지닭강정", R.drawable.korea_cb_danyang_chicken, "음식", 4.1f, "충북 단양군 단양읍 도전4길 30 115호 단양흑마늘닭강정", 36.98274089999999f, 128.3694901f, "ChIJz5BM7EX1YzURsx1lTeS5z7Y", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("충주", Arrays.asList(
                new Place("충주댐 벚꽃길", R.drawable.korea_cb_chungju_dam, "댐/자연", 4.4f, "충북 충주시 동량면 조동리 산180-11", 37.0105935f, 127.9903446f, "ChIJmxvOUXF_ZDURKtRsW4J9C1c", "user_default", "group_default"),
                new Place("목계나루터", R.drawable.korea_cb_chungju_mokgye, "명소", 4.0f, "충북 충주시 엄정면 목계리 309", 37.0767219f, 127.8840351f, "ChIJT6mobi2CZDURGmKsqhVhCko", "user_default", "group_default"),
                new Place("우림정원", R.drawable.korea_cb_chungju_woorim, "정원", 4.1f, "충북 충주시 엄정면 삼실길 42", 37.079083f, 127.9036405f, "ChIJjZs7A1qBZDURPuSQDIFj7G0", "user_default", "group_default"),
                new Place("하방마을 벚꽃길", R.drawable.korea_cb_chungju_cherryblossom, "벚꽃길", 4.1f, "충청북도 충주시 하방천변길 282", 36.9814794f, 127.8972292f, "ChIJpaSwRMCHZDUR_rx4UEQhpFs", "user_default", "group_default"),
                new Place("게으른악어", R.drawable.korea_cb_chungju_lazyalligator, "카페", 4.2f, "충북 충주시 살미면 월악로 927", 36.9237849f, 128.0480556f, "ChIJy6F4kWl9ZDURRWxSexd0t1g", "user_default", "group_default"),
                new Place("청진궁중면옥 충주본점", R.drawable.korea_cb_chungju_naengmyeon, "음식", 4.0f, "충북 충주시 충원대로 166 1층", 36.9437148f, 127.9005205f, "ChIJcefDxraIZDURHnBgxl0hITo", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("제천", Arrays.asList(
                new Place("청풍호반 케이블카", R.drawable.korea_cb_jecheon_cheongpungho, "호수", 4.5f, "충북 제천시 청풍면 문화재길 166", 37.00132660000001f, 128.1666109f, "ChIJVzKCDsN3ZDURHzhGQY58C44", "user_default", "group_default"),
                new Place("도화리도토리밥상", R.drawable.korea_cb_jecheon_dohwari, "마을", 4.0f, "충북 제천시 청풍면 옥순봉로16길 2-9 도화리도토리밥상", 36.9920744f, 128.1871935f, "ChIJQWLZoNN3ZDURU5mzInUGKAs", "user_default", "group_default"),
                new Place("청풍랜드", R.drawable.korea_cb_jecheon_cheongpungland, "테마파크", 4.2f, "충북 제천시 청풍면 청풍호로50길 6", 37.0130556f, 128.1772222f, "ChIJlaN5-TqIYzURJIwHHbf8zPA", "user_default", "group_default"),
                new Place("청풍황금떡갈비", R.drawable.korea_cb_jecheon_tteokgalbi, "음식", 4.1f, "충북 제천시 청풍면 청풍호로 1682", 37.0206786f, 128.1726194f, "ChIJ4U34FD-IYzURacXXZzB9NEc", "user_default", "group_default"),
                new Place("용천막국수", R.drawable.korea_cb_jecheon_makguksu, "음식", 4.0f, "충북 제천시 의병대로 165 용천막국수 본점", 37.1340049f, 128.2188732f, "ChIJ8xj_2x-OYzURgd0TCK3yMDQ", "user_default", "group_default"),
                new Place("콘크리트월", R.drawable.korea_cb_jecheon_concretewall, "카페", 3.9f, "충북 제천시 금성면 청풍호로 1566 콘크리트월", 37.0289963f, 128.1752957f, "ChIJlTMcx7GJYzURcHlUBlztjPk", "user_default", "group_default")
        ));


        // ========== 전라남도 ==========
        REGION_TO_DESTINATIONS.put("전라남도", Arrays.asList(
                new TravelDestination("여수", R.drawable.korea_jn_yeosu),
                new TravelDestination("담양", R.drawable.korea_jn_damyang),
                new TravelDestination("해남", R.drawable.korea_jn_haenam)
        ));
        DESTINATION_TO_PLACES.put("여수", Arrays.asList(
                new Place("금오도 비렁길", R.drawable.korea_jn_yeosu_geumodo, "자연", 4.5f, "전남 여수시 남면 유송리 산284", 34.537627f, 127.7078224f, "ChIJq6qqqtrAbTURsSwqDrmtw8s", "user_default", "group_default"),
                new Place("만성리 검은모래해변", R.drawable.korea_jn_yeosu_manseongri, "해변", 4.3f, "전남 여수시 만흥동", 34.7773718f, 127.7448858f, "ChIJEeJ_TfJ3bjURi9pdbHjhFss", "user_default", "group_default"),
                new Place("이사부크루즈", R.drawable.korea_jn_yeosu_isabucruise, "체험", 4.4f, " 전남 여수시 돌산읍 돌산로 3617-18", 34.72986139999999f, 127.7369175f, "ChIJFeOtYibZbTUR6yYfTjNTiIQ", "user_default", "group_default"),
                new Place("오동도", R.drawable.korea_jn_yeosu_odongdo, "섬/공원", 4.2f, "전남 여수시 수정동 산1-11", 34.7446658f, 127.7664977f, "ChIJ04TP1GHYbTURa17mljtqOMQ", "user_default", "group_default"),
                new Place("바다김밥 본점", R.drawable.korea_jn_yeosu_badagimbap, "음식", 4.2f, "전남 여수시 통제영5길 10-4", 34.740779f, 127.7359368f, "ChIJv_nbvlXZbTURpuwVWNuN6aA", "user_default", "group_default"),
                new Place("순이네 밥상", R.drawable.korea_jn_yeosu_suninebapsang, "음식", 4.1f, "전남 여수시 통제영5길 5", 34.7405583f, 127.7354963f, "ChIJHRMuiZrZbTURQ_EZSoDvy2w", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("담양", Arrays.asList(
                new Place("죽녹원", R.drawable.korea_jn_damyang_juknokwon, "정원", 4.5f, "담양군 담양읍 죽녹원로 119", 35.3266303f, 126.9853868f, "ChIJe8P5p_LqcTURrVA5YOtPKRk", "user_default", "group_default"),
                new Place("명옥헌 원림", R.drawable.korea_jn_damyang_myeongokheon, "자연", 4.4f, "전라남도 담양군 고서면 후산길 103", 35.2195491f, 126.9963076f, "ChIJQ_OakQftcTURyNR_MOKRl9Q", "user_default", "group_default"),
                new Place("죽화경", R.drawable.korea_jn_damyang_jukhwagyeong, "정원", 4.3f, "전남 담양군 봉산면 유산길 73-16", 35.25244190000001f, 126.9641795f, "ChIJjdAqRc_scTURGkatArKez18", "user_default", "group_default"),
                new Place("담양 관방제림", R.drawable.korea_jn_damyang_gwanbangjerim, "명소", 4.2f, "전남 담양군 담양읍 객사7길 37", 35.32356219999999f, 126.9891545f, "ChIJh9DpPovqcTURaR4LbmXyqmI", "user_default", "group_default"),
                new Place("쌍교숯불갈비 담양본점", R.drawable.korea_jn_damyang_ssanggyo, "음식", 4.4f, "전남 담양군 봉산면 송강정로 212", 35.2532105f, 126.9521294f, "ChIJZ-SwmjKTcTURLbZKGnnohLY", "user_default", "group_default"),
                new Place("트루와지엠", R.drawable.korea_jn_damyang_truwasiam, "음식", 4.0f, "전남 담양군 수북면 동상길 78", 35.3008446f, 126.9438477f, "ChIJ07zrbyaVcTURx-1I6hPy8aY", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("해남", Arrays.asList(
                new Place("포레스트 수목원", R.drawable.korea_jn_haenam_forestarboretum, "수목원", 4.3f, "전남 해남군 현산면 봉동길 232-118", 34.4530167f, 126.5955106f, "ChIJHfGSrIwCczURzfwf4PzCJAE", "user_default", "group_default"),
                new Place("명량해상케이블카", R.drawable.korea_jn_haenam_cablecar, "산/자연", 4.2f, "전남 해남군 문내면 관광레저로 12-20 ", 34.572815f, 126.3088293f, "ChIJGwV-lh0NczURTlKRTsMCohw", "user_default", "group_default"),
                new Place("땅끝송호해변", R.drawable.korea_jn_haenam_ttanggeut, "해변", 4.1f, "전남 해남군 송지면 땅끝해안로 1827", 34.3153344f, 126.5186253f, "ChIJC4K7PtIYczUR5_ffNvX9G-I", "user_default", "group_default"),
                new Place("문가든", R.drawable.korea_jn_haenam_moongarden, "카페", 4.0f, "전남 해남군 계곡면 오류골길 64", 34.6328087f, 126.6538627f, "ChIJe9p2hCFTcjURfUVlZsNhbsE", "user_default", "group_default"),
                new Place("소망식당", R.drawable.korea_jn_haenam_somang, "음식", 4.0f, "전남 해남군 해남읍 구교2길 2", 34.5759895f, 126.594535f, "ChIJjR1efaWqczURTY7UJfKX9Lo", "user_default", "group_default"),
                new Place("서성식당", R.drawable.korea_jn_haenam_seoseong, "음식", 3.9f, "전남 해남군 해남읍 중앙1로 282", 34.5738889f, 126.5925f, "ChIJAQAA8K-qczURkO1XJ7JVE9g", "user_default", "group_default")
        ));


        // ========== 경기도 ==========
        REGION_TO_DESTINATIONS.put("경기도", Arrays.asList(
                new TravelDestination("수원", R.drawable.korea_gg_suwon),
                new TravelDestination("여주", R.drawable.korea_gg_yeoju),
                new TravelDestination("화성", R.drawable.korea_gg_hwaseong)
        ));
        DESTINATION_TO_PLACES.put("수원", Arrays.asList(
                new Place("일월수목원", R.drawable.korea_gg_suwon_ilwol, "수목원", 4.6f, "경기 수원시 장안구 일월로 61 일월수목원", 37.28809849999999f, 126.9763495f, "ChIJl8Rhv8dCezURIb5_SH12N7w", "user_default", "group_default"),
                new Place("르디투어 광교", R.drawable.korea_gg_suwon_leditour, "카페", 4.2f, "경기 수원시 영통구 웰빙타운로36번길 46-234 1~3층", 37.31204529999999f, 127.0550374f, "ChIJA7z_h6dbezURDPocWuue-8k", "user_default", "group_default"),
                new Place("스타필드", R.drawable.korea_gg_suwon_starfield, "쇼핑몰", 4.5f, "경기 수원시 장안구 수성로 175 스타필드 수원", 37.2873665f, 126.9912075f, "ChIJV-ZOvmdDezURVYEkKRoAi4o", "user_default", "group_default"),
                new Place("아쿠아플라넷", R.drawable.korea_gg_suwon_aquaplanet, "아쿠아리움", 4.3f, "경기 수원시 영통구 광교중앙로 124 갤러리아 광교 파사쥬 지하 1층", 37.2850853f, 127.0570997f, "ChIJeYwpoMJbezURoBBwF9Srdj8", "user_default", "group_default"),
                new Place("평장원 본점", R.drawable.korea_gg_suwon_pyeongjangwon, "음식", 4.0f, "경기 수원시 팔달구 인계로108번길 23 1층, 2층", 37.266625f, 127.0293633f, "ChIJw8KDz2JDezUR0UvNxgAE2nM", "user_default", "group_default"),
                new Place("리위크", R.drawable.korea_gg_suwon_leweek, "카페", 4.1f, "경기 수원시 팔달구 창룡대로38번길 16-2 카페 리위크", 37.28248479999999f, 127.0214824f, "ChIJnWwOdXZDezURdECD2VS2y-k", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("여주", Arrays.asList(
                new Place("루덴시아", R.drawable.korea_gg_yeoju_ludensia, "테마파크", 4.4f, "경기 여주시 산북면 금품1로 177 여주 루덴시아", 37.4020598f, 127.4610864f, "ChIJldV-bgxQYzURkD3nTQEI5rI", "user_default", "group_default"),
                new Place("산오마켓", R.drawable.korea_gg_yeoju_sanomarket, "시장", 4.0f, "경기 여주시 금사면 금품1로 468 1층 산오마켓", 37.40525890000001f, 127.4813194f, "ChIJTdMDRQBbYzURlziiBqkx7BI", "user_default", "group_default"),
                new Place("은아목장", R.drawable.korea_gg_yeoju_eunahmokjang, "목장", 4.1f, "경기 여주시 가남읍 금당5길 139 은아목장", 37.21259630000001f, 127.620231f, "ChIJO2iE1nhfYzURRXJ6VWZp15Q", "user_default", "group_default"),
                new Place("천서리막국수 본점", R.drawable.korea_gg_yeoju_cheonseori, "음식", 4.3f, "경기 여주시 대신면 여양로 1974", 37.4026363f, 127.5466132f, "ChIJQ4koQaJEYzURJ0SWOnxpwac", "user_default", "group_default"),
                new Place("감성식탁밥집", R.drawable.korea_gg_yeoju_gamsung, "음식", 3.9f, "경기 여주시 칠산길 39", 37.289074f, 127.6444796f, "ChIJFZIRT21eYzURxFIw5WhAoEA", "user_default", "group_default"),
                new Place("강계봉진막국수", R.drawable.korea_gg_yeoju_ganggye, "음식", 4.2f, "경기 여주시 대신면 천서리길 26", 37.4031841f, 127.5489424f, "ChIJd-ZPWp9EYzUREZe0wzl54OM", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("화성", Arrays.asList(
                new Place("서해랑 케이블카", R.drawable.korea_gg_hwaseong_cablecar, "케이블카", 4.5f, "경기 화성시 서신면 전곡항로 1-10 서해랑 제부도해상케이블카", 37.1839482f, 126.650166f, "ChIJZcBLOUsJezURs0T1aczrT-w", "user_default", "group_default"),
                new Place("궁평항", R.drawable.korea_gg_hwaseong_gungpyeong, "항구", 4.2f, "화성시 서신면 궁평항로 1049-24", 37.1162794f, 126.6809772f, "ChIJieTuLEEJezUR26m-d4DU9Z4", "user_default", "group_default"),
                new Place("힐링숲 불멍카페", R.drawable.korea_gg_hwaseong_healingforest, "카페", 4.0f, "경기 화성시 외삼미로15번길 75-105 1층", 37.23375679999999f, 127.2015691f, "ChIJ8YBesrNRezURolpCdeh3StM", "user_default", "group_default"),
                new Place("율암온천", R.drawable.korea_gg_hwaseong_yulam, "온천", 4.1f, "경기 화성시 팔탄면 온천로 434-14", 37.1521318f, 126.882198f, "ChIJiVPGMIwUezURkPcD8GHC1x8", "user_default", "group_default"),
                new Place("섹션", R.drawable.korea_gg_hwaseong_section, "명소", 3.8f, "경기 화성시 남양읍 신남안길 293 섹션", 37.1910824f, 126.8202956f, "ChIJ2cYCNfgTezURxEAlTB0ukos", "user_default", "group_default"),
                new Place("왕골남서문장작불곰탕 본점", R.drawable.korea_gg_hwaseong_wanggol, "음식", 4.3f, "경기 화성시 남양읍 화성로 1238", 37.21496680000001f, 126.8189776f, "ChIJyaBnJS9tezUR-8WRMzIeEKE", "user_default", "group_default")
        ));


        // ========== 강원도 ==========
        REGION_TO_DESTINATIONS.put("강원도", Arrays.asList(
                new TravelDestination("강릉", R.drawable.korea_gy_gangneong),
                new TravelDestination("속초", R.drawable.korea_gy_sokcho),
                new TravelDestination("양양", R.drawable.korea_gy_yangyang)
        ));
        DESTINATION_TO_PLACES.put("강릉", Arrays.asList(
                new Place("사근진해변", R.drawable.korea_gy_gangneung_sageunjin, "해변", 4.5f, "강원 강릉시 해안로604번길 16", 37.812513f, 128.8993589f, "ChIJC18WeQPkYTUR9CB4M6kP-f8", "user_default", "group_default"),
                new Place("안반데기", R.drawable.korea_gy_gangneung_anbandaegi, "자연", 4.6f, "강원 강릉시 왕산면 안반데기길 428", 37.622629f, 128.7391555f, "ChIJ97uUgSmNYTURTljwVmCli5A", "user_default", "group_default"),
                new Place("정동진 레일바이크", R.drawable.korea_gy_gangneung_jeongdongjin_railbike, "체험", 4.4f, "강원 강릉시 강동면 정동역길 17", 37.69145839999999f, 129.0326174f, "ChIJixBhfLvDYTURkQ3kfm9Lo1w", "user_default", "group_default"),
                new Place("감자바우", R.drawable.korea_gy_gangneung_gamjabawu, "음식", 4.2f, "강원 강릉시 금성로35번길 4", 37.7530424f, 128.8970484f, "ChIJV6f3z_vlYTURKbN9pxDOPOQ", "user_default", "group_default"),
                new Place("카페툇마루", R.drawable.korea_gy_gangneung_cafe_toetmaru, "카페", 4.1f, "강원 강릉시 난설헌로 232 카페 툇마루", 37.79283909999999f, 128.9145879f, "ChIJdQtjvprmYTURSTjeNERJgIU", "user_default", "group_default"),
                new Place("순두부 젤라또", R.drawable.korea_gy_gangneung_sundubu_gelato, "음식", 4.0f, "강원 강릉시 초당순두부길 95-5", 37.7916416f, 128.9154846f, "ChIJmwLdcwPnYTUR-Vpr6GRnFO0", "user_default", "group_default")
        ));
        DESTINATION_TO_PLACES.put("속초", Arrays.asList(
                new Place("속초 해수욕장", R.drawable.korea_gy_sokcho_beach, "해변", 4.4f, "강원 속초시 조양동", 38.1905148f, 128.6035251f, "ChIJS2kcIEa72F8RJfsMfa2hhr8", "user_default", "group_default"),
                new Place("설악산 케이블카", R.drawable.korea_gy_sokcho_seoraksan_cablecar, "케이블카", 4.7f, "강원 속초시 설악산로 1085", 38.1730998f, 128.4890543f, "ChIJU6refB6j2F8Rhvwfrw2-Hes", "user_default", "group_default"),
                new Place("속초아이 대관람차", R.drawable.korea_gy_sokcho_eye, "놀이기구", 4.3f, "강원 속초시 청호해안길 2", 38.1907881f, 128.6027924f, "ChIJS79nuZe72F8R-bDr18hSnbI", "user_default", "group_default"),
                new Place("초당 본점", R.drawable.korea_gy_sokcho_chodang, "음식", 4.2f, "강원 속초시 관광로 440 초당본점", 38.19912659999999f, 128.5360482f, "ChIJW2rB6kG92F8Ro-D8SS72dOg", "user_default", "group_default"),
                new Place("88생선구이", R.drawable.korea_gy_sokcho_fish_grill, "음식", 4.5f, "강원 속초시 중앙부두길 71", 38.2037191f, 128.5918819f, "ChIJKWvbmXa82F8RyEb9nn5oIg4", "user_default", "group_default"),
                new Place("카페 긷", R.drawable.korea_gy_sokcho_cafe_gid, "카페", 4.0f, "강원 속초시 원암학사평길 60 (노학동)",38.2066886f, 128.5146353f, "ChIJ8flmila92F8ROXEdo7_Yl0I", "user_default", "group_default" )
        ));
        DESTINATION_TO_PLACES.put("양양", Arrays.asList(
                new Place("하조대 해변", R.drawable.korea_gy_yangyang_hajodae, "해변", 4.5f, "강원 양양군 현북면 하광정리 29리", 38.0243746f, 128.723294f, "ChIJ6zmVbqNU318RfhLzbee6XnQ", "user_default", "group_default"),
                new Place("서피비치", R.drawable.korea_gy_yangyang_surfyy_beach, "해변", 4.4f, "강원 양양군 현북면 하조대해안길 119", 38.0289168f, 128.7175256f, "ChIJpYfYTlKr2F8R1OSLkIBTVJA", "user_default", "group_default"),
                new Place("하조대 전망대", R.drawable.korea_gy_yangyang_hajodae_lighthouse, "등대/전망대", 4.3f, "강원 양양군 현북면 하륜길 54", 38.0223462f, 128.7290946f, "ChIJVTGO-upV318R0JcDjRLLS64", "user_default", "group_default"),
                new Place("송이버섯마을", R.drawable.korea_gy_yangyang_songi_village, "음식", 4.1f, "강원 양양군 양양읍 남대천로 55-20", 38.06522330000001f, 128.6211936f, "ChIJHUb7oUyp2F8RVsPAOGqLSsc", "user_default", "group_default"),
                new Place("설온", R.drawable.korea_gy_yangyang_seolon, "카페", 4.0f, "강원 양양군 강현면 복골길201번길 58 설온", 38.142254f, 128.5656548f, "ChIJ4-hYDZ2l2F8RmS6KtCtuWfU", "user_default", "group_default"),
                new Place("바다뷰 제빵소", R.drawable.korea_gy_yangyang_bakery, "카페", 4.0f, "강원 양양군 강현면 동해대로 3296", 38.1335735f, 128.6209177f, "ChIJ98EM6nGv2F8RHvEbzX1RItA", "user_default", "group_default")
        ));


        // 기본값 (region이 null이거나 매칭되지 않을 경우)
        REGION_TO_DESTINATIONS.put("default", Arrays.asList(
                new TravelDestination("제주", R.drawable.korea_jj_jejusi) // 기본 여행지 이미지도 변경
        ));
        DESTINATION_TO_PLACES.put("제주", Arrays.asList(
                new Place("성산일출봉", R.drawable.korea_jj_seogwipo_seongsan, "자연", 4.7f, "제주시 성산읍 성산리 1번지", 33.45805600000001f, 126.9425f, "ChIJn3jj9rkUDTURS7YjOgUyUVU", "user_default", "group_default"),
                new Place("한라산", R.drawable.korea_jj_jejusi_hallasan, "자연", 4.8f, "제주시 한라산국립공원", 33.3616666f, 126.5291666f, "ChIJRxSQ5FH_DDURJGgM5HCZeJ4", "user_default", "group_default"),
                new Place("천지연폭포", R.drawable.korea_jj_seogwipo_cheonjiyeon, "자연", 4.5f, "서귀포시 천지연로 663", 33.246944f, 126.554417f, "ChIJhYbR5ZdTDDURtgGb2uZzuA0", "user_default", "group_default"),
                new Place("동문시장", R.drawable.korea_jj_jejusi_dongmun, "시장/음식", 4.2f, "제주시 관덕로14길 20", 33.5120448f, 126.5282794f, "ChIJpyFxA1TjDDUR1kddP-9mN0Y", "user_default", "group_default")
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
        public float latitude;
        public float longitude;
        public String placeId;
        public String userId; // 이 장소를 선택한 사용자 ID
        public String group; // 이 장소를 선택한 사용자의 그룹

        public Place(String name, int imageResId, String category, float rating, String address,
                     float latitude, float longitude, String placeId, String userId, String group) {
            this.name = name;
            this.imageResId = imageResId;
            this.category = category;
            this.rating = rating;
            this.address = address;
            this.isChecked = false;
            this.latitude = latitude;
            this.longitude = longitude;
            this.placeId = placeId;
            this.userId = userId;
            this.group = group;
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

    public static List<Place> getPlacesForDestination(String destination) {
        return DESTINATION_TO_PLACES.getOrDefault(destination, Collections.emptyList());
    }
}