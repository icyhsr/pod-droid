package trainedge.pod_droid;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.octo.android.robospice.SpiceManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import trainedge.pod_droid.fragments.BaseFragment;
import trainedge.pod_droid.fragments.FragmentDetail;
import trainedge.pod_droid.fragments.FragmentHome;
import trainedge.pod_droid.restclient.service.MyRssPodcastService;
import trainedge.pod_droid.tools.Constants;
import trainedge.pod_droid.tools.Interfaces.NetworkActions;
import trainedge.pod_droid.tools.Interfaces.TempFragmentListener;
import trainedge.pod_droid.tools.Util;

import static trainedge.pod_droid.adapters.CategoryAdapter.CAT_NAME;
import static trainedge.pod_droid.adapters.CategoryAdapter.CAT_POS;

@EActivity(R.layout.activity_home)
public class PodcastViewerActivity extends AppCompatActivity implements NetworkActions, TempFragmentListener {

    /**********************************************************************************************
     *
     *                                      Simple variables
     *
     **********************************************************************************************/


    private String mTitle;


    /**********************************************************************************************
     *
     *                                      Special variables
     *
     **********************************************************************************************/


    private SpiceManager spiceManager = new SpiceManager(MyRssPodcastService.class);

    // FragmentManager manager;

    MyCustomFragmaneManager mManager;

    /**********************************************************************************************
     *
     *                                          UI Variables
     *
     **********************************************************************************************/


    @ViewById
    View loader;
    private String category_name;
    private String url;
    private SharedPreferences pod_pref;


    /**********************************************************************************************
     *
     *                                      Activity's lifecycle
     *
     **********************************************************************************************/

    @Override
    protected void onStart() {
        super.onStart();
        setupPodcast();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (spiceManager.isStarted())
            spiceManager.shouldStop();
    }

    @Override
    public void onBackPressed() {

        mManager.removeLast();
    }

    @AfterViews
    void init() {
        initLogicalComponents();

    }


    /**********************************************************************************************
     *
     *                                      Public methods
     *
     **********************************************************************************************/


    /**********************************************************************************************
     *
     *                                      Private methods
     *
     **********************************************************************************************/
    private void initLogicalComponents() {

        mManager = new MyCustomFragmaneManager(getFragmentManager());
        mManager.insertConstantFragment(Constants.TAG_FRAG_HOME, true);


    }


    private void showLoading(boolean show) {
        if (show)
            loader.setVisibility(View.VISIBLE);
        else
            loader.setVisibility(View.GONE);
    }


    public void addTmpFragment(String tag, Bundle args) {
        Fragment fragmentInsert = null;
        if (tag == Constants.TAG_FRAG_DETAIL) {
            FragmentDetail fragmentDetail = (FragmentDetail) getFragmentManager()
                    .findFragmentByTag(Constants.TAG_FRAG_DETAIL);
            if (fragmentDetail == null) {
                fragmentDetail = new FragmentDetail();//TODO change for AA Framgment
                fragmentDetail.setArguments(args);
            }
            fragmentInsert = fragmentDetail;
        }


        Util.le("MANAGER " + mManager);
        mManager.insert(fragmentInsert, "temp");
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setTitle(mTitle);
    }

    protected void showCloseDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(getString(R.string.home_exit_dialog_message));
        alert.setCancelable(true);
        alert.setPositiveButton(getString(R.string.home_exit_ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alert.setNegativeButton(getString(R.string.home_exit_cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.create().show();
    }

    /**********************************************************************************************
     *
     *                                      Inner classes
     *
     **********************************************************************************************/


    private class MyCustomFragmaneManager {
        private ArrayList<String> tags;
        private int index;
        private FragmentManager manager;
        private int iFragmentCounter;

        public MyCustomFragmaneManager(FragmentManager fragmentManager) {
            manager = fragmentManager;
            tags = new ArrayList<String>();
            index = 0;
            iFragmentCounter = 0;
        }

        public boolean hasFragment(String tag) {
            Fragment fragmentToFind = null;

            fragmentToFind = manager.findFragmentByTag(tag);

            return fragmentToFind != null;
        }

        public void showConstantFragment(String tag, boolean clearTags) {
            Fragment constantFragment = manager.findFragmentByTag(tag);
            if (clearTags) {
                tags.clear();
                index = 0;
            }

            if (!tag.equalsIgnoreCase(Constants.TAG_FRAG_HOME)) {
                tags.add(Constants.TAG_FRAG_HOME);
                index++;
            }

            tags.add(tag);
            index++;
            manager.beginTransaction().show(constantFragment).commit();
        }

        public void insert(Fragment _fragment, String _prefix) {
            String fragmentTag = "";

            if (_prefix.contains(Constants.TAG_PREFIX_CONST)) {
                fragmentTag = _prefix;
            } else {
                fragmentTag = _prefix + "_" + iFragmentCounter;
            }

            Util.li("New fragment tag" + fragmentTag);

            if (tags.size() > 0) {
                manager.beginTransaction().hide(getLastFragment()).commit();
            }
            tags.add(fragmentTag);

            if (tags.size() > 1) {
                Fragment lastFragment = manager.findFragmentByTag(tags.get(index - 1));
                if (tags.size() < 60) {
                    manager.beginTransaction().add(R.id.container, _fragment, fragmentTag).commit();
                } else {
                    Fragment firstFragment = manager.findFragmentByTag(tags.get(0));
                    if (firstFragment.getTag().contains(Constants.TAG_PREFIX_CONST)) {
                        manager.beginTransaction().hide(firstFragment).commit();
                    } else {
                        manager.beginTransaction().remove(firstFragment).commit();
                    }
                    tags.remove(0);
                    manager.beginTransaction().add(R.id.container, _fragment, fragmentTag).commit();

                    index--;
                }

            } else {
                manager.beginTransaction().add(R.id.container, _fragment, fragmentTag).commit();
            }
            index++;
            iFragmentCounter++;
        }

        public Fragment getLastFragment() {
            return getFragmentManager().findFragmentByTag(tags.get(tags.size() - 1));
        }

        public void removeLast() {
            if (tags.size() == 1 && (tags.get(index - 1).equalsIgnoreCase(Constants.TAG_FRAG_HOME))) {
                showCloseDialog();
            } else if (tags.size() > 0) {

                manager = getFragmentManager();
                Util.ld("App" + "Mayor que 0: Fragment a eliminar: " + tags.get(index - 1));
                Fragment lastFragment = manager.findFragmentByTag(tags.get(index - 1));
                Fragment newLastFragment;
                Util.li("Tags: " + tags.size());
                if (tags.size() > 1) {
                    Util.ld("App" + "Mayor que 1: index: " + index);
                    newLastFragment = manager.findFragmentByTag(tags.get(index - 2));
                    manager.beginTransaction().show(newLastFragment).commit();

                    if (newLastFragment instanceof BaseFragment) {
                        mTitle = ((BaseFragment) newLastFragment).getTitle();
                        restoreActionBar();
                    }
                    restoreActionBar();
                }

                if (lastFragment.getTag().contains(Constants.TAG_PREFIX_CONST)) {
                    Util.li("Escondiendo a :" + lastFragment.getTag());
                    manager.beginTransaction().hide(lastFragment).commit();
                } else {
                    Util.le("Borrado: " + lastFragment.getTag());
                    manager.beginTransaction().remove(lastFragment).commit();
                }

                tags.remove(index - 1);
                index--;

                if (tags.size() == 0) {
                    if (this.hasFragment(Constants.TAG_FRAG_HOME)) {
                        this.showConstantFragment(Constants.TAG_FRAG_HOME, true);
                    }
                }
            }
        }

        public void insertConstantFragment(String fragmentToKeep, boolean clearTemporalFragments) {
            Util.le("LLamando a: removeTemporalFragments: tags size: " + tags.size());
            ArrayList<String> tagsToRemove = new ArrayList<String>();

            if (clearTemporalFragments) {
                //Escondiendo los fragmentos constantes
                for (String tag : tags) {
                    if (tag.contains(Constants.TAG_PREFIX_CONST)) {
                        Fragment fragmentToHide = manager.findFragmentByTag(tag);
                        manager.beginTransaction().hide(fragmentToHide).commit();

                    }
                }

                //Removiendo los fragmentos temporales
                for (String tag : tags) {
                    if (!tag.contains(Constants.TAG_PREFIX_CONST)) {
                        Util.li("App" + "Fragment " + tag + " eliminado.");
                        Fragment fragmentToRemove = manager.findFragmentByTag(tag);
                        manager.beginTransaction().remove(fragmentToRemove).commit();
                        tagsToRemove.add(tag);
                    }
                }

                //Removiendo las etiquetas de los fragmentos temporales
                for (String tag : tagsToRemove) {
                    tags.remove(tag);
                    index--;
                }
            } else {
                if (tags.size() > 1) {
                    Fragment lastFragment = manager.findFragmentByTag(tags.get(index - 1));
                    manager.beginTransaction().hide(lastFragment).commit();
                }
            }

            //Mostrar o agregar fragment home
            if (fragmentToKeep.equals(Constants.TAG_FRAG_HOME)) {
                if (!hasFragment(Constants.TAG_FRAG_HOME)) {
                    Util.li("Inserting new constant fragment home");
                    insert(new FragmentHome(), Constants.TAG_FRAG_HOME);
                } else {
                    Util.li("Showing constant fragment home");
                    showConstantFragment(Constants.TAG_FRAG_HOME, clearTemporalFragments);
                }
            } else {
                Util.le("Error on removeTemporalFragments: Could not get wich constant fragment to add.");
            }

            Util.li("Tags: " + tags.size());
            Util.li("Index: " + index);
        }

        public Fragment getConstantFragment(String tag) {
            return manager.findFragmentByTag(tag);
        }
    }

    @Override
    public void inserTemporalFragmnet(String tag, Bundle args) {
        addTmpFragment(tag, args);
    }

    @Override
    public void showLoader(boolean show) {
        showLoading(show);
    }

    private void setupPodcast() {
        if (getIntent() != null) {
            Intent intent = getIntent();
            category_name = getIntent().getStringExtra(CAT_NAME);
            int pos = getIntent().getIntExtra(CAT_POS, 0);
            url = getResources().getStringArray(R.array.podcast)[pos];
            getSupportActionBar().setSubtitle(category_name);
            pod_pref = getSharedPreferences("pod_pref", Context.MODE_PRIVATE);
            pod_pref.edit().putString("selected_url", url).apply();

        }
    }

}
