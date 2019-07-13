package com.lokeshmudgal06.vinsolproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleMeetingFragment extends Fragment implements View.OnClickListener {

    private static final String MEETING_DESCRIPTION = "meeting_description";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat timeDateFormat = new SimpleDateFormat("hh:mm aa");

    private TextView backTv, meetingDateTv, startTimeTv, endTimeTv, submitTv;
    private Calendar meetingDateSelected, startTimeSelected, endTimeSelected;

    private List<MeetingModel> meetingModelList;

    private EditText meetingDescription;

    private boolean isDateChanged;
    private String dateForApi;

    private ApiInterface apiInterface;
    private LinearLayout progressContainerLl;

    public static ScheduleMeetingFragment newInstance(Calendar selectedCalender, List<MeetingModel> meetingModelList) {

        ScheduleMeetingFragment fragment = new ScheduleMeetingFragment();
        fragment.meetingDateSelected = selectedCalender;
        fragment.meetingModelList = meetingModelList;
        fragment.isDateChanged = false;
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(meetingDescription.getText()))
            outState.putString(MEETING_DESCRIPTION, meetingDescription.getText().toString());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_meeting, container, false);
        backTv = view.findViewById(R.id.back);
        meetingDateTv = view.findViewById(R.id.meeting_date_tv);
        meetingDateTv.setText(dateFormat.format(meetingDateSelected.getTime()));
        meetingDescription = view.findViewById(R.id.meeting_description);

        startTimeTv = view.findViewById(R.id.start_time_tv);
        endTimeTv = view.findViewById(R.id.end_time_tv);
        submitTv = view.findViewById(R.id.submit_tv);
        progressContainerLl = view.findViewById(R.id.progress_container);

        if (savedInstanceState != null && savedInstanceState.containsKey(MEETING_DESCRIPTION)) {
            meetingDescription.setText(savedInstanceState.getString(MEETING_DESCRIPTION));
        }

        if (startTimeSelected != null) {

            startTimeTv.setText(timeDateFormat.format(startTimeSelected.getTime()));
        }

        if (endTimeSelected != null) {

            endTimeTv.setText(timeDateFormat.format(endTimeSelected.getTime()));
        }

        backTv.setOnClickListener(this);
        meetingDateTv.setOnClickListener(this);
        startTimeTv.setOnClickListener(this);
        endTimeTv.setOnClickListener(this);
        submitTv.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                getActivity().onBackPressed();
                break;
            case R.id.meeting_date_tv:
                openDatePicker();
                break;
            case R.id.start_time_tv:
                if (startTimeSelected == null)
                    startTimeSelected = Calendar.getInstance();
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTimeSelected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startTimeSelected.set(Calendar.MINUTE, minute);
                        startTimeSelected.set(Calendar.SECOND, 0);
                        startTimeSelected.set(Calendar.MILLISECOND, 0);


                        startTimeTv.setText(timeDateFormat.format(startTimeSelected.getTime()));
                    }
                }, startTimeSelected.get(Calendar.HOUR_OF_DAY), startTimeSelected.get(Calendar.MINUTE), false).show();
                break;
            case R.id.end_time_tv:
                if (endTimeSelected == null)
                    endTimeSelected = Calendar.getInstance();
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endTimeSelected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        endTimeSelected.set(Calendar.MINUTE, minute);
                        endTimeSelected.set(Calendar.SECOND, 0);
                        endTimeSelected.set(Calendar.MILLISECOND, 0);

                        endTimeTv.setText(timeDateFormat.format(endTimeSelected.getTime()));
                    }
                }, endTimeSelected.get(Calendar.HOUR_OF_DAY), endTimeSelected.get(Calendar.MINUTE), false).show();
                break;
            case R.id.submit_tv:

                if (startTimeSelected.compareTo(endTimeSelected) >= 0){
                    Toast.makeText(getActivity(), "Please select valid start and end time", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isDateChanged) {
                    getMeetingList();
                    return;
                } else {
                    if (!isSlotAvailable()) {
                        Toast.makeText(getActivity(), "Slot Not Available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(getActivity(), "Slot Available", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }

    private boolean isSlotAvailable() {
        Log.e("AlucarD", "start selected " + dateFormat.format(startTimeSelected.getTime()) + " " + timeDateFormat.format(startTimeSelected.getTime())
                + " end selected " + dateFormat.format(endTimeSelected.getTime()) + " " + timeDateFormat.format(endTimeSelected.getTime()));
        for (int i = 0; i < meetingModelList.size(); i++) {
            MeetingModel model = meetingModelList.get(i);
            Calendar start = getFormattedTime(model.getStartTime());
            Calendar end = getFormattedTime(model.getEndTime());
            Log.e("AlucarD", "start " + dateFormat.format(start.getTime()) + " " + timeDateFormat.format(start.getTime()) +
                    " end " + dateFormat.format(end.getTime()) + " " + timeDateFormat.format(end.getTime()));

            Log.e("AlucarD", String.valueOf(startTimeSelected.compareTo(start)));
            if (startTimeSelected.compareTo(start) > 0 && startTimeSelected.compareTo(end) < 0)
                return false;

            if (endTimeSelected.compareTo(start) > 0 && endTimeSelected.compareTo(end) < 0)
                return false;

            if (startTimeSelected.compareTo(start) <= 0 && endTimeSelected.compareTo(end) >= 0)
                return false;
        }
        return true;
    }

    private Calendar getFormattedTime(String endTime) {
        Calendar calendar = Calendar.getInstance();
        String[] timeArray = endTime.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0].trim()));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1].trim()));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    private void getMeetingList() {
        dateForApi = dateFormat.format(meetingDateSelected.getTime());
        progressContainerLl.setVisibility(View.VISIBLE);
        Call<List<MeetingModel>> call = apiInterface.getMeetingList(dateForApi);
        Log.e("AlucarD", call.request().url().toString());
        call.enqueue(new Callback<List<MeetingModel>>() {
            @Override
            public void onResponse(Call<List<MeetingModel>> call, Response<List<MeetingModel>> response) {

                if (response.isSuccessful()) {

                    isDateChanged = false;
                    meetingModelList = response.body();
                    if (!isSlotAvailable()) {
                        Toast.makeText(getActivity(), "Slot Not Available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(getActivity(), "Slot Available", Toast.LENGTH_SHORT).show();

                }

                progressContainerLl.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<MeetingModel>> call, Throwable t) {
                progressContainerLl.setVisibility(View.GONE);
            }
        });
    }

    private void openDatePicker() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                isDateChanged = true;
                meetingDateSelected.set(Calendar.YEAR, year);
                meetingDateSelected.set(Calendar.MONTH, month);
                meetingDateSelected.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                meetingDateTv.setText(dateFormat.format(meetingDateSelected.getTime()));
            }
        }, meetingDateSelected.get(Calendar.YEAR), meetingDateSelected.get(Calendar.MONTH), meetingDateSelected.get(Calendar.DAY_OF_MONTH));
        ;
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }

}
