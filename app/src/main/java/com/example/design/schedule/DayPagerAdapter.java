// DayPagerAdapter.java
package com.example.design.schedule;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.design.detail.OnPlaceSelectedListener; // ⭐ import 경로 확인

import java.util.HashMap;
import java.util.List;

public class DayPagerAdapter extends FragmentStateAdapter {

    private List<String> dayTitles;
    private OnPlaceSelectedListener placeSelectedListener;

    // Fragment들을 저장하여 필요할 때 접근할 수 있도록 맵 추가
    private final HashMap<Integer, DayFragment> registeredFragments = new HashMap<>();

    // 생성자 수정: OnPlaceSelectedListener를 매개변수로 받도록 합니다.
    public DayPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> dayTitles, OnPlaceSelectedListener listener) {
        super(fragmentActivity);
        this.dayTitles = dayTitles;
        this.placeSelectedListener = listener; // 리스너 초기화
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // DayFragment 생성 시 dayPosition과 리스너를 함께 전달합니다.
        // DayFragment 내부에서 onAttach를 통해 placeSelectedListener를 받으므로,
        // 여기서는 dayPosition만 newInstance에 전달합니다.
        DayFragment fragment = DayFragment.newInstance(dayTitles.get(position), position);
        registeredFragments.put(position, fragment); // 생성된 Fragment 등록
        return fragment;
    }

    @Override
    public int getItemCount() {
        return dayTitles.size();
    }

    /**
     * ViewPager에서 특정 Fragment 인스턴스를 가져올 수 있는 메서드입니다.
     * @param position 가져올 Fragment의 뷰페이저 내 인덱스
     * @return 해당 인덱스의 DayFragment 인스턴스
     */
    public DayFragment getFragment(int position) { // ⭐ 오타 수정 완료: 'int int position' -> 'int position'
        return registeredFragments.get(position);
    }
}