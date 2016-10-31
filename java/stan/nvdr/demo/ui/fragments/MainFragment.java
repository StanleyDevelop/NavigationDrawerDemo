package stan.nvdr.demo.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

    private TextView style;
    private TextView substyle;

    private TextView speed;
    private TextView ios_offset;
    private TextView tweaking;

    private MainFragmentBehaviour behaviour;
    private boolean iOsStyle;
    private boolean scaleStyle;
    private boolean edge;

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.speed_factor_1:
                    speedFactor(1);
                    break;
                case R.id.speed_factor_2:
                    speedFactor(2);
                    break;
                case R.id.speed_factor_3:
                    speedFactor(3);
                    break;
                case R.id.ios_style_switch:
                    iOsStyle = !iOsStyle;
                    iosSwitch();
                    break;
                case R.id.edge_switch:
                    edge = !edge;
                    edgeSwitch();
                    break;
                case R.id.ios_offset_1:
                    iosOffset(1);
                    break;
                case R.id.ios_offset_2:
                    iosOffset(12);
                    break;
                case R.id.ios_offset_5:
                    iosOffset(5);
                    break;
                case R.id.tweaking_1:
                    setTweaking(1);
                    break;
                case R.id.tweaking_2:
                    setTweaking(2);
                    break;
                case R.id.tweaking_5:
                    setTweaking(5);
                    break;
                case R.id.tweaking_10:
                    setTweaking(10);
                    break;
                case R.id.scale_style_switch:
                    scaleStyle = !scaleStyle;
                    scaleStyleSwitch();
                    break;
            }
        }
    };

    private void setTweaking(int i)
    {
        tweaking.setText("" + i);
        behaviour.tweaking(i);
    }

    private void iosOffset(int i)
    {
        ios_offset.setText("" + i);
        behaviour.iosOffset(i);
    }

    private void speedFactor(int i)
    {
        speed.setText("" + i);
        behaviour.speedFactor(i);
    }

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
        style = (TextView)v.findViewById(R.id.style);
        substyle = (TextView)v.findViewById(R.id.substyle);
        speed = (TextView)v.findViewById(R.id.speed);
        ios_offset = (TextView)v.findViewById(R.id.ios_offset);
        tweaking = (TextView)v.findViewById(R.id.tweaking);
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
        v.findViewById(R.id.scale_style_switch).setOnClickListener(clickListener);
    }
    private void init()
    {
        iOsStyle = true;
        iosSwitch();
        scaleStyle = false;
        ios_style_switch.setOnClickListener(clickListener);
        edge = true;
        edge_switch.setOnClickListener(clickListener);
        setTweaking(1);
        iosOffset(1);
        speedFactor(1);
    }

    private void iosSwitch()
    {
        if(iOsStyle)
        {
            ios_style_switch.setText("ios");
            style.setText("ios");
        }
        else
        {
            ios_style_switch.setText("and");
            style.setText("android");
            scaleStyle = false;
            substyle.setText("");
        }
        behaviour.iosSwitch(iOsStyle);
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
    private void scaleStyleSwitch()
    {
        if(scaleStyle)
        {
            substyle.setText("scale style on");
            iOsStyle = true;
            ios_style_switch.setText("ios");
            style.setText("ios");
        }
        else
        {
            substyle.setText("");
        }
        behaviour.scaleStyleSwitch(scaleStyle);
    }

    public interface MainFragmentBehaviour
    {
        void iosSwitch(boolean style);
        void iosOffset(float o);
        void edgeSwitch(boolean edge);
        void speedFactor(float f);
        void tweaking(float f);
        void scaleStyleSwitch(boolean s);
    }
}