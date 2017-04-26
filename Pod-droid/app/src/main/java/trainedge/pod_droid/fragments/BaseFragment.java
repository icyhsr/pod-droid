package trainedge.pod_droid.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.octo.android.robospice.SpiceManager;

import trainedge.pod_droid.R;
import trainedge.pod_droid.restclient.service.MyRssPodcastService;
import trainedge.pod_droid.tools.Interfaces.NetworkActions;
import trainedge.pod_droid.tools.Interfaces.TempFragmentListener;
import trainedge.pod_droid.tools.Util;

public abstract class BaseFragment extends Fragment {


    private NetworkActions networkListener;
    private TempFragmentListener fragmentListener;
    private String titleActionBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            networkListener=(NetworkActions)context;
            fragmentListener=(TempFragmentListener) context;
        }catch (ClassCastException e){
            leh("Must implement the interfaces");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        if(spiceManager.isStarted())
            spiceManager.shouldStop();
    }

    protected SpiceManager spiceManager = new SpiceManager(MyRssPodcastService.class);

    private boolean enableOwnLog = true;
    protected void disableLog(){
        enableOwnLog = false;
    }

    protected void le(String message){
        if (enableOwnLog){
            Util.le(message);
        }
    }

    protected void leh(String message){
        if (enableOwnLog){
            Util.le(message, true);
        }
    }

    protected void li(String message){
        if (enableOwnLog){
            Util.li(message);
        }
    }

    protected void lih(String message){
        if (enableOwnLog){
            Util.li(message, true);
        }
    }

    protected void ld(String message){
        if (enableOwnLog){
            Util.ld(message);
        }
    }

    protected void ldh(String message){
        if (enableOwnLog){
            Util.ld(message, true);
        }
    }

    protected void lw(String message){
        if (enableOwnLog){
            Util.lw(message);
        }
    }

    protected void lwh(String message){
        if (enableOwnLog){
            Util.lw(message, true);
        }
    }

    public String getTitle() {
        return titleActionBar != null ? titleActionBar :getString(R.string.app_name);
    }
    protected boolean isEmpty(String string){
        return Util.isEmpty(string);
    }

    protected void showView(View view){
        Util.showView(view);
    }

    protected void showView(int id){
        Util.showView(getView().findViewById(id));
    }

    protected void hideView(View view){
        Util.hideView(view);
    }

    protected void hideView(int id){
        Util.hideView(getView().findViewById(id));
    }

    protected View find(int id){
        return getView().findViewById(id);
    }

    protected void toast(String text){
        Util.showToast(getActivity(), text);
    }

    protected void hideKb(){
        Util.hideKeyboard(getActivity());
    }

    protected SpiceManager getSpiceManager(){
        return spiceManager;
    }

    protected void showLoading(boolean show){
        if(networkListener!=null)
            networkListener.showLoader(show);
    }
    protected void insertTemporal(String tag,Bundle args){
        fragmentListener.inserTemporalFragmnet(tag,args);
    }



}
