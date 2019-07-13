package com.lokeshmudgal06.vinsolproject.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lokeshmudgal06.vinsolproject.model.MeetingModel;
import com.lokeshmudgal06.vinsolproject.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MeetingListAdapter extends RecyclerView.Adapter<MeetingListAdapter.ViewHolder> {

    private SimpleDateFormat timeDateFormat = new SimpleDateFormat("hh:mm aa");
    private List<MeetingModel> meetingModelList;

    public void setList(List<MeetingModel> meetingModelList){
        this.meetingModelList = meetingModelList;
        notifyDataSetChanged();
    }
    public MeetingListAdapter(List<MeetingModel> meetingModelList) {
        this.meetingModelList = meetingModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_meeting_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        MeetingModel meetingModel = meetingModelList.get(i);

        viewHolder.startTimeTv.setText(getFormattedTime(meetingModel.getStartTime()));
        viewHolder.endTimeTv.setText(getFormattedTime(meetingModel.getEndTime()));

        viewHolder.descriptionTv.setText(meetingModel.getDescription());

        String participants = meetingModel.getParticipants().toString();
        viewHolder.participantTv.setText(participants.substring(1, participants.length() - 1));
    }

    private String getFormattedTime(String endTime) {
        Calendar calendar = Calendar.getInstance();
        String[] timeArray = endTime.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0].trim()));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1].trim()));

        return timeDateFormat.format(calendar.getTime());
    }

    @Override
    public int getItemCount() {
        return meetingModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView startTimeTv;
        private TextView endTimeTv;
        private TextView descriptionTv;
        private TextView participantTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            startTimeTv = itemView.findViewById(R.id.start_time);
            endTimeTv = itemView.findViewById(R.id.end_time);
            descriptionTv = itemView.findViewById(R.id.description);
            participantTv = itemView.findViewById(R.id.participants);
        }
    }
}
