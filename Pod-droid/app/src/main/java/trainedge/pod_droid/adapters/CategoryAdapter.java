package trainedge.pod_droid.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import trainedge.pod_droid.Model.CategoryModel;
import trainedge.pod_droid.PodcastActivity;
import trainedge.pod_droid.PodcastViewerActivity;

import trainedge.pod_droid.PodcastViewerActivity_;
import trainedge.pod_droid.R;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    public static final String CAT_NAME = "trainedge.pod_droid.adapters.name";
    public static final String CAT_POS = "trainedge.pod_droid.adapters.pos";
    private final List<CategoryModel> list;
    private final Context context;

    public CategoryAdapter(Context context) {
        list = new ArrayList<>();
        String[] categoryArray = context.getResources().getStringArray(R.array.podcast);
        String[] categoryCommon = context.getResources().getStringArray(R.array.common_name);
        for (int i = 0; i < categoryArray.length; i++) {
            list.add(new CategoryModel(categoryCommon[i], categoryArray[i]));
        }
        this.context = context;
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = ((LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.simple_categories_item, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryHolder holder, final int position) {
        final CategoryModel model = list.get(position);
        holder.tvCatName.setText(model.name);
        holder.tvCatName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(holder.tvCatName.getContext(), PodcastActivity.class);
                i.putExtra(CAT_NAME, model.name);
                i.putExtra(CAT_POS, position);
                holder.tvCatName.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        TextView tvCatName;

        public CategoryHolder(View itemView) {
            super(itemView);
            tvCatName = (TextView) itemView.findViewById(R.id.tvCatName);
        }
    }
}
