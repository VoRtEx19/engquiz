package com.example.engquiz.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.engquiz.R;
import com.example.engquiz.ResultActivity;

import java.util.Arrays;

public class Fragment2 extends Fragment implements ResultActivity.onAnswerListener  {
    @Override
    public void sendResult(boolean isCorrect) {
        this.isChecked = true;
        View v = getViewByPosition(selected, answers);
        v.setBackgroundColor(getResources().getColor(isCorrect ? R.color.green : R.color.red));
    }

    boolean isChecked = false;
    public TextView question;
    public ListView answers;
    public String q;
    public String[] a;
    public int ra, selected = 0;

    ResultActivity.onAnswerListener answerListener;

    public Fragment2(String q, String[] a, int ra) {
        this.q = q;
        this.a = a;
        this.ra = ra;
        Log.d("gotElements", q + Arrays.toString(a) + ra);
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            answerListener = (ResultActivity.onAnswerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        question = view.findViewById(R.id.question2);
        question.setText(q);
        answers = view.findViewById(R.id.answers2);
        answers.setAdapter(new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_list_item_single_choice, a));
        answers.setOnItemClickListener(this::onItemClick);
        return view;
    }

    private void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
        if (!isChecked) {
            for (int i = 0; i < answers.getAdapter().getCount(); i++)
                getViewByPosition(i, answers).setBackgroundColor(Color.WHITE);
            itemClicked.setBackgroundColor(getResources().getColor(R.color.black_overlay));
            selected = position;
            answerListener.sendResult(position == ra - 1);
        }
    }

    private View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
