// DayPagerAdapter.java
package com.example.design.schedule;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.design.detail.OnPlaceSelectedListener;

import java.util.HashMap;
import java.util.List;

public class DayPagerAdapter extends FragmentStateAdapter {

    private List<String> dayTitles;
    private OnPlaceSelectedListener placeSelectedListener;
    private String travelScheduleId; // ⭐ 추가
    private String groupId;          // ⭐ 추가

    private final HashMap<Integer, DayFragment> registeredFragments = new HashMap<>();

    // ⭐ 생성자 수정: travelScheduleId와 groupId를 매개변수로 받도록 합니다.
    public DayPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> dayTitles,
                           OnPlaceSelectedListener listener, String travelScheduleId, String groupId) {
        super(fragmentActivity);
        this.dayTitles = dayTitles;
        this.placeSelectedListener = listener;
        this.travelScheduleId = travelScheduleId; // ⭐ 초기화
        this.groupId = groupId;                  // ⭐ 초기화
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // ⭐ DayFragment 생성 시 travelScheduleId와 groupId를 함께 전달합니다.
        DayFragment fragment = DayFragment.newInstance(dayTitles.get(position), position, travelScheduleId, groupId);
        registeredFragments.put(position, fragment);
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
    public Fragment getFragment(int position) {
        return registeredFragments.get(position);
    }
}