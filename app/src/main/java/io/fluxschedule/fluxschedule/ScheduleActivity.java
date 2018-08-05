package io.fluxschedule.fluxschedule;

import android.widget.*;
import android.os.*;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.helper.*;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import java.util.*;
import android.view.*;

public class ScheduleActivity extends AppCompatActivity{

    private LinearLayout unfocuser;
    private RecyclerView sRecyclerView;
    private ScheduleAdapter sAdapter;
    private RecyclerView.LayoutManager sLayoutManager;
    public ArrayList<ScheduleItem> scheduleItems = new ArrayList<ScheduleItem>();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);


        unfocuser = (LinearLayout) findViewById(R.id.unfocuser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addTaskButton = (FloatingActionButton) findViewById(R.id.fab);
        addTaskButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v){
                scheduleItems.add(new ScheduleItem("New Item", 30));
                sAdapter.notifyItemInserted(scheduleItems.size() - 1);
                sAdapter.update();
                unfocus();
            }
        });
        sRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        sRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        sLayoutManager = new LinearLayoutManager(this);
        sRecyclerView.setLayoutManager(sLayoutManager);
        // Create an `ItemTouchHelper` and attach it to the `RecyclerView`
        ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
        ith.attachToRecyclerView(sRecyclerView);


        // specify an adapter (see also next example)
        sAdapter = new ScheduleAdapter(this, scheduleItems);
        sRecyclerView.setAdapter(sAdapter);
    }

    ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
        boolean firstMove = true;
        int firstPos = 0;
        int lastPos = 0;
        ArrayList<ScheduleItem> lockedTasks = new ArrayList<ScheduleItem>();
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            // get the viewHolder's and target's positions in your adapter data, swap them

            int initial = viewHolder.getAdapterPosition();
            int curr = target.getAdapterPosition();

            Collections.swap(scheduleItems, viewHolder.getAdapterPosition(), target.getAdapterPosition());
            if(curr == 0) {
                scheduleItems.get(0).startTime -= scheduleItems.get(0).duration;

            }
            sAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());


            sAdapter.quickReloadCells();

            if(firstMove) {
                lockedTasks = sAdapter.getLockedTasks();
                //sAdapter.flashCell(initial, true);
                firstPos = viewHolder.getAdapterPosition();
                firstMove = false;
            }
            lastPos = curr;
            sAdapter.flashCellsBetween(firstPos, target.getAdapterPosition());

            return true;
        }

        @Override
        public void clearView(RecyclerView view, RecyclerView.ViewHolder viewholder) {
            super.clearView(view, viewholder);
            //sAdapter.flashCell(lastPos, false);
            sAdapter.unflashAllCells();
            sAdapter.update(lockedTasks);
            lockedTasks = new ArrayList<ScheduleItem>();
            firstMove = true;
            firstPos = 0;
            lastPos = 0;
        }
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //TODO
        }

        //defines the enabled move directions in each state (idle, swiping, dragging).
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                    ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public void unfocus() {
        unfocuser.requestFocus();
        unfocuser.clearFocus();
    }
}

