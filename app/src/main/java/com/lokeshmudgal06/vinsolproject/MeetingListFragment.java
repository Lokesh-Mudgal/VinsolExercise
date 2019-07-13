package com.lokeshmudgal06.vinsolproject;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingListFragment extends Fragment implements View.OnClickListener {

    private static final String MEETING = "meeting";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat dayDateFormat = new SimpleDateFormat("EEEE");
    private TextView dateTv, nextTv, prevTv, scheduleMeetingTv;
    private RecyclerView meetingListRv;
    private LinearLayout progressContainerLl;

    private MeetingListAdapter meetingListAdapter;

    private List<MeetingModel> meetingModelList;
    private ApiInterface apiInterface;
    private Calendar calendar;

    private String dayOfTheDay, dateForApi;

    public static MeetingListFragment newInstance() {

        MeetingListFragment fragment = new MeetingListFragment();
        return fragment;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(MEETING, (ArrayList) meetingModelList);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_list, container, false);
        meetingListRv = view.findViewById(R.id.meeting_list);
        dateTv = view.findViewById(R.id.date_tv);
        nextTv = view.findViewById(R.id.next);
        prevTv = view.findViewById(R.id.prev);
        scheduleMeetingTv = view.findViewById(R.id.schedule_meeting);
        progressContainerLl = view.findViewById(R.id.progress_container);

        meetingListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL);
        meetingListRv.addItemDecoration(dividerItemDecoration);


        nextTv.setOnClickListener(this);
        prevTv.setOnClickListener(this);
        scheduleMeetingTv.setOnClickListener(this);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dayOfTheDay = dayDateFormat.format(calendar.getTime());
        dateForApi = dateFormat.format(calendar.getTime());

        if (savedInstanceState != null && savedInstanceState.containsKey(MEETING)) {
            meetingModelList = savedInstanceState.getParcelableArrayList(MEETING);
            setMeetingAdaptor();
        } else {
            getMeetingList();
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dateTv.setText(dayOfTheDay + ", " + dateForApi);
        } else {
            dateTv.setText(dateForApi);
        }
    }

    private void setMeetingAdaptor() {
        meetingListAdapter = new MeetingListAdapter(meetingModelList);
        meetingListRv.setAdapter(meetingListAdapter);
    }

    private void getMeetingList() {

        progressContainerLl.setVisibility(View.VISIBLE);
        Call<List<MeetingModel>> call = apiInterface.getMeetingList(dateForApi);
        Log.e("AlucarD", call.request().url().toString());
        call.enqueue(new Callback<List<MeetingModel>>() {
            @Override
            public void onResponse(Call<List<MeetingModel>> call, Response<List<MeetingModel>> response) {

                if (response.isSuccessful()) {

                    meetingModelList = response.body();

                    setMeetingAdaptor();

                }

                progressContainerLl.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<MeetingModel>> call, Throwable t) {
                progressContainerLl.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                checkDateValidation();
                setDateAndGetData();
                break;
            case R.id.prev:
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                checkDateValidation();
                setDateAndGetData();
                break;
            case R.id.schedule_meeting:


                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, ScheduleMeetingFragment.newInstance(calendar, meetingModelList))
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    private void checkDateValidation() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        Log.e("AlucarD", (currentDate.getTimeInMillis()) + "  " + (calendar.getTimeInMillis()));
        if (calendar.getTime().before(currentDate.getTime())) {
            scheduleMeetingTv.setEnabled(false);
            scheduleMeetingTv.setBackground(getResources().getDrawable(R.drawable.grey_button_back));
            scheduleMeetingTv.setTextColor(getResources().getColor(R.color.secondaryText));
        } else {
            scheduleMeetingTv.setEnabled(true);
            scheduleMeetingTv.setBackground(getResources().getDrawable(R.drawable.button_back));
            scheduleMeetingTv.setTextColor(Color.WHITE);
        }

    }

    private void setDateAndGetData() {

        dateForApi = dateFormat.format(calendar.getTime());
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dateTv.setText(dayDateFormat.format(calendar.getTime()) + ", " + dateForApi);
        } else {
            dateTv.setText(dateForApi);
        }

        getMeetingList();

    }
}
