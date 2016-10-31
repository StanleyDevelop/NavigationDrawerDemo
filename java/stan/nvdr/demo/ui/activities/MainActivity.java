package stan.nvdr.demo.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import stan.nvdr.demo.DrawerContainer;
import stan.nvdr.demo.R;
import stan.nvdr.demo.ui.fragments.MainFragment;
import stan.nvdr.demo.ui.fragments.MenuFragment;

public class MainActivity
    extends AppCompatActivity
{
    private DrawerContainer drawerContainer;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.main_activity);
        initViews();
        init();
    }
    private void initViews()
    {
        drawerContainer = (DrawerContainer)findViewById(R.id.main_drawer);
    }
    private void init()
    {
        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.main_frame, MainFragment.newInstanse(new MainFragment.MainFragmentBehaviour()
                                   {
                                       @Override
                                       public void iosSwitch(boolean style)
                                       {
                                           drawerContainer.setIosStyle(style);
                                       }

                                       @Override
                                       public void iosOffset(float o)
                                       {
                                           drawerContainer.setIosOffset(o);
                                       }

                                       @Override
                                       public void edgeSwitch(boolean edge)
                                       {
                                           drawerContainer.setEdge(edge);
                                       }

                                       @Override
                                       public void speedFactor(float f)
                                       {
                                           drawerContainer.setSpeedFactor(f);
                                       }

                                       @Override
                                       public void tweaking(float f)
                                       {
                                           drawerContainer.setTweaking(f);
                                       }

                                       @Override
                                       public void scaleStyleSwitch(boolean s)
                                       {
                                           drawerContainer.setScaleStyle(s);
                                       }
                                   }))
                                   .commit();
        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.menu_frame, new MenuFragment())
                                   .commit();
    }
}