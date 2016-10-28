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
        v.findViewById(R.id.speed_factor_1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                behaviour.speedFactor(1);
            }
        });
        v.findViewById(R.id.speed_factor_2).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                behaviour.speedFactor(2);
            }
        });
        v.findViewById(R.id.speed_factor_3).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                behaviour.speedFactor(3);
            }
        });
    }
    private void init()
    {
        style = true;
        ios_style_switch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                style = !style;
                iosSwitch();
            }
        });
        edge = true;
        edge_switch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                edge = !edge;
                edgeSwitch();
            }
        });
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
        void edgeSwitch(boolean edge);
        void speedFactor(float f);
    }
}