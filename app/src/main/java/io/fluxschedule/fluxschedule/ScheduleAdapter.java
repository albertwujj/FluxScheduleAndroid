package io.fluxschedule.fluxschedule;

import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.*;
import android.view.*;
import java.util.*;
import java.math.BigInteger;
import android.widget.*;
import android.view.inputmethod.*;
import android.animation.ValueAnimator.*;
import android.animation.*;
import android.graphics.Color;
import android.support.v4.graphics.*;
import android.view.View;

import io.blackbox_vision.datetimepickeredittext.view.DatePickerEditText;
import io.blackbox_vision.datetimepickeredittext.view.TimePickerEditText;

/**
 * Created by albertwu on 3/3/18.
 */



public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    int purple = ColorUtils.setAlphaComponent(Integer.parseInt("961ff2", 16), 255/3);
    private ArrayList<ScheduleItem> scheduleItems = new ArrayList<ScheduleItem>();
    private Context mContext;
    private RecyclerView mRecyclerView;
    private HashSet<Integer> blueItems = new HashSet<Integer>();


    LinearLayoutManager layout;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public EditText startTimeET;
        public EditText nameET;
        public EditText durationET;
        public ToggleButton lockButton;
        public ViewHolder(View v) {
            super(v);
            mView = v;

            nameET = (EditText)v.findViewById(R.id.task_name);
            durationET = (EditText)v.findViewById(R.id.task_duration);
            startTimeET = (EditText)v.findViewById(R.id.task_start_time);
            lockButton = (ToggleButton) v.findViewById(R.id.lock_button);
            nameET.setSelectAllOnFocus(true);
            durationET.setSelectAllOnFocus(true);
            startTimeET.setSelectAllOnFocus(true);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ScheduleAdapter(Context mContext, ArrayList<ScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
        this.mContext = mContext;
        scheduleItems.add(new ScheduleItem("Morning Routine", 30));
        scheduleItems.add(new ScheduleItem("Check Facebook", 15));
        scheduleItems.add(new ScheduleItem("Work", 8 * 60));
        scheduleItems.add(new ScheduleItem("Donuts w/ co-workers", 35));
        scheduleItems.add(new ScheduleItem("Respond to emails", 30));
        scheduleItems.add(new ScheduleItem("Work on side-project", 45));
        scheduleItems.add(new ScheduleItem("Pick up Benjamin", 30));
        update();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_row_view, parent, false);
        final ViewHolder vh = new ViewHolder(v);



        return vh;
    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return scheduleItems.size();
    }

    public void update() {
        recalculateTimes();
        this.notifyDataSetChanged();


    }
    public void update(ArrayList<ScheduleItem> lockedTasks) {
        recalculateTimes(lockedTasks);
        this.notifyDataSetChanged();

    }
    public void recalculateTimes(ArrayList<ScheduleItem> lockedTasks) {
        deleteLockedTasks();
        recalcBasic();
        for(int i = 0; i <lockedTasks.size(); i++) {
            insertItem(lockedTasks.get(i));
            recalcBasic();
        }
    }
    public void recalculateTimes() {
        ArrayList<ScheduleItem> lockedTasks = deleteLockedTasks();
        recalcBasic();
        for(int i = 0; i <lockedTasks.size(); i++) {
            insertItem(lockedTasks.get(i));
            recalcBasic();
        }
    }
    public void recalcBasic(){
        if(scheduleItems.size() == 0) {
            return;
        }
        int currStartTime = scheduleItems.get(0).startTime;
        for(int i = 0; i < scheduleItems.size(); i++){
            ScheduleItem scheduleItem = scheduleItems.get(i);

            scheduleItem.startTime = currStartTime;

            currStartTime += scheduleItem.duration;

        }
    }
    public void recalcDisplay(){
        if(scheduleItems.size() == 0) {
            return;
        }
        int currStartTime = scheduleItems.get(0).startTime;
        for(int i = 0; i < scheduleItems.size(); i++){
            ScheduleItem scheduleItem = scheduleItems.get(i);
            if(!scheduleItem.locked) {
                scheduleItem.startTime = currStartTime;
            }
            currStartTime += scheduleItem.duration;

        }
    }
    public ArrayList<ScheduleItem> getLockedTasks() {
        ArrayList<ScheduleItem> ret = new ArrayList<ScheduleItem>();
        for(int i = 0; i < scheduleItems.size(); i++){
            ScheduleItem scheduleItem = scheduleItems.get(i);
            if(scheduleItem.locked) {
                ret.add(scheduleItem);
            }
        }
        return ret;
    }
    public ArrayList<ScheduleItem> deleteLockedTasks() {
        ArrayList<ScheduleItem> ret = new ArrayList<ScheduleItem>();
        for(int i = 0; i < scheduleItems.size(); i++){
            ScheduleItem scheduleItem = scheduleItems.get(i);
            if(scheduleItem.locked) {
                scheduleItems.remove(i);
                ret.add(scheduleItem);
                i--;
            }
        }
        return ret;
    }
    public void insertItem(ScheduleItem insert){
        int target = 0;
        ScheduleItem curr = new ScheduleItem("je", 0);
        for(int i = 0; i < scheduleItems.size(); i++){
            curr = scheduleItems.get(i);
            if(curr.startTime < insert.startTime && curr.startTime + curr.duration > insert.startTime) {
                target = i + 1;
                break;
            }
        }
        int splitDuration = curr.startTime + curr.duration - insert.startTime;
        curr.duration -= splitDuration;
        scheduleItems.add(target, insert);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
        layout = (LinearLayoutManager) mRecyclerView.getLayoutManager();
    }
    public void quickReloadCells() {
        recalcDisplay();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            for(int i = layout.findFirstVisibleItemPosition(); i <= layout.findLastVisibleItemPosition(); i++){
                ScheduleItem scheduleItem = scheduleItems.get(i);
                    final EditText startTimeET = (EditText) layout.findViewByPosition(i).findViewById(R.id.task_start_time);

                    startTimeET.setText(scheduleItem.getStartTimeDate());


            }
        }
        }, 10);
    }


    public void flashCellsBetween(int pos1, int pos2) {

        if(pos2 < pos1) {
            int temp = pos2;
            pos2 = pos1;
            pos1 = temp;
        }
        final int pos1F = pos1;
        final int pos2F = pos2;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                for(int i = layout.findFirstVisibleItemPosition(); i <= layout.findLastVisibleItemPosition(); i++){
                    ScheduleItem scheduleItem = scheduleItems.get(i);
                    final EditText startTimeET = (EditText) layout.findViewByPosition(i).findViewById(R.id.task_start_time);

                    int colorFrom = purple;
                    int colorTo = Color.WHITE;

                    if(!blueItems.contains(i)){
                        colorFrom = Color.WHITE;
                    }
                    if(i >= pos1F && i <= pos2F) {
                        colorTo = purple;
                        blueItems.add(i);
                        startTimeET.setTag(purple);
                    }else {
                        blueItems.remove(i);
                        startTimeET.setTag(Color.WHITE);
                    }




                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(250); // milliseconds
                    colorAnimation.addUpdateListener(new AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            startTimeET.setBackgroundColor((int) animator.getAnimatedValue());
                        }

                    });
                    colorAnimation.start();
                }

            }}, 1);
    }

    public void unflashAllCells(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                for(int i = layout.findFirstVisibleItemPosition(); i <= layout.findLastVisibleItemPosition(); i++){
                        final EditText startTimeET = (EditText) layout.findViewByPosition(i).findViewById(R.id.task_start_time);
                        ScheduleItem scheduleItem = scheduleItems.get(i);
                        int colorFrom = purple;
                        if(startTimeET.getTag() != null && (int)startTimeET.getTag() == purple) {
                            colorFrom = Color.WHITE;
                        }


                        int colorTo = Color.WHITE;
                        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        colorAnimation.setDuration(800); // milliseconds
                        colorAnimation.addUpdateListener(new AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                startTimeET.setBackgroundColor((int) animator.getAnimatedValue());


                            }

                        });
                        colorAnimation.start();

                        startTimeET.setTag(Color.WHITE);


                }

            }
        }, 10);
    }

    public void flashCell(final int pos, final boolean flipOn) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ScheduleItem scheduleItem = scheduleItems.get(pos);

                if(pos <= layout.findLastVisibleItemPosition() && layout.findFirstVisibleItemPosition() <= pos) {
                    final EditText startTimeET = (EditText) layout.findViewByPosition(pos).findViewById(R.id.task_start_time);
                    int colorFrom = Color.WHITE;
                    int colorTo = purple;
                    if(!flipOn) {
                        colorFrom = purple;
                        colorTo = Color.WHITE;
                    }
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(250); // milliseconds
                    colorAnimation.addUpdateListener(new AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            startTimeET.setBackgroundColor((int) animator.getAnimatedValue());
                        }

                    });
                    colorAnimation.start();
                }

            }}, 10);
    }
    /*
    public void toggleFlashCell(final int pos){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int colorFrom = Color.WHITE;
                int colorTo = ColorUtils.setAlphaComponent(Color.BLUE, 255/3);
                ScheduleItem scheduleItem = scheduleItems.get(pos);
                if(blueItems.contains(pos)) {
                    colorTo= Color.WHITE;
                    colorFrom = ColorUtils.setAlphaComponent(Color.BLUE, 255/3);
                    blueItems.remove(pos);
                } else {
                    blueItems.add(pos);
                }
                if(pos <= layout.findLastVisibleItemPosition() && layout.findFirstVisibleItemPosition() <= pos) {
                    final EditText startTimeET = (EditText) layout.findViewByPosition(pos).findViewById(R.id.task_start_time);


                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(250); // milliseconds
                    colorAnimation.addUpdateListener(new AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            startTimeET.setBackgroundColor((int) animator.getAnimatedValue());
                        }

                    });
                    colorAnimation.start();
                }

            }}, 10);
    }
*/
// Replace the contents of a view (invoked by the layout manager)
@Override
public void onBindViewHolder(ViewHolder holder, final int position) {
    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    final int finalPos = position;
    final ScheduleItem scheduleItem = scheduleItems.get(position);
    final EditText startTimeET = holder.startTimeET;
    final EditText nameET = holder.nameET;
    final EditText durationET = holder.durationET;
    final ToggleButton lockButton = holder.lockButton;
    startTimeET.setText(scheduleItem.getStartTimeDate());
    nameET.setText(scheduleItem.name);
    durationET.setText(scheduleItem.getDurationDate());


    nameET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                ScheduleAdapter.this.scheduleItems.get(finalPos).name = v.getText().toString();
                update();
                return false;
            }
            return false;
        }
    });



    durationET.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = scheduleItem.getDurHours();
            int minute = scheduleItem.getDurMinutes();
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(mContext, 2,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    scheduleItem.duration = selectedHour * 60 + selectedMinute;
                    update();
                }
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Duration");
            mTimePicker.show();
        }
    });
    startTimeET.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = scheduleItem.getStartHours();
            int minute = scheduleItem.getStartMinutes();
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(mContext, 2,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    if(position != 0) {
                        scheduleItem.startTime = selectedHour * 60 + selectedMinute;
                        ScheduleItem prev = scheduleItems.get(position - 1);
                        prev.duration = scheduleItem.startTime - prev.startTime;
                    }
                    update();
                }
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
    });
    /*
    lockButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                scheduleItem.locked = true;
            } else {
                scheduleItem.locked = false;
            }
        }
    });
    lockButton.setChecked(scheduleItem.locked); */
}
}
