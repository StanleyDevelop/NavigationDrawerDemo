package stan.nvdr.demo.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import stan.nvdr.demo.R;

public class MainFragment
        extends Fragment
{
    static public MainFragment newInstanse(MainFragmentBehaviour b)
    {
        MainFragment fragment = new MainFragment();
        fragment.behaviour = b;
        return fragment;
    }

    private Button ios_style_switch;
    private Button edge_switch;

    private MainFragmentBehaviour behaviour;
    private boolean style;
    private boolean edge;

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.speed_factor_1:
                    behaviour.speedFactor(1);
                    break;
                case R.id.speed_factor_2:
                    behaviour.speedFactor(2);
                    break;
                case R.id.speed_factor_3:
                    behaviour.speedFactor(3);
                    break;
                case R.id.ios_style_switch:
                    style = !style;
                    iosSwitch();
                    break;
                case R.id.edge_switch:
                    edge = !edge;
                    edgeSwitch();
                    break;
                case R.id.ios_offset_1:
                    behaviour.iosOffset(1);
                    break;
                case R.id.ios_offset_2:
                    behaviour.iosOffset(2);
                    break;
                case R.id.ios_offset_5:
                    behaviour.iosOffset(5);
                    break;
                case R.id.tweaking_1:
                    behaviour.tweaking(1);
                    break;
                case R.id.tweaking_2:
                    behaviour.tweaking(2);
                    break;
                case R.id.tweaking_5:
                    behaviour.tweaking(5);
                    break;
                case R.id.tweaking_10:
                    behaviour.tweaking(10);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.main_fragment, container, false);
        initViews(v);
        init();
        return v;
    }

    private void initViews(View v)
    {
        ios_style_switch = (Button)v.findViewById(R.id.ios_style_switch);
        edge_switch = (Button)v.findViewById(R.id.edge_switch);
        v.findViewById(R.id.speed_factor_1).setOnClickListener(clickListener);
        v.findViewById(R.id.speed_factor_2).setOnClickListener(clickListener);
        v.findViewById(R.id.speed_factor_3).setOnClickListener(clickListener);
        v.findViewById(R.id.ios_offset_1).setOnClickListener(clickListener);
        v.findViewById(R.id.ios_offset_2).setOnClickListener(clickListener);
        v.findViewById(R.id.ios_offset_5).setOnClickListener(clickListener);
        v.findViewById(R.id.tweaking_1).setOnClickListener(clickListener);
        v.findViewById(R.id.tweaking_2).setOnClickListener(clickListener);
        v.findViewById(R.id.tweaking_5).setOnClickListener(clickListener);
        v.findViewById(R.id.tweaking_10).setOnClickListener(clickListener);
    }
    private void init()
    {
        style = true;
        ios_style_switch.setOnClickListener(clickListener);
        edge = true;
        edge_switch.setOnClickListener(clickListener);
    }

    private void iosSwitch()
    {
        if(style)
        {
            ios_style_switch.setText("ios");
        }
        else
        {
            ios_style_switch.setText("and");
        }
        behaviour.iosSwitch(style);
    }
    private void edgeSwitch()
    {
        if(edge)
        {
            edge_switch.setText("edge");
        }
        else
        {
            edge_switch.setText("full");
        }
        behaviour.edgeSwitch(edge);
    }

    public interface MainFragmentBehaviour
    {
        void iosSwitch(boolean style);
        void iosOffset(float o);
        void edgeSwitch(boolean edge);
        void speedFactor(float f);
        void tweaking(float f);
    }
}