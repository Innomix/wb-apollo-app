package me.fmtech.apollo.module.task.create;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.fmtech.apollo.R;
import me.fmtech.apollo.model.bean.LocationBean;
import me.fmtech.apollo.widget.DefaultItemTouchHelpCallback;
import me.fmtech.apollo.widget.DefaultItemTouchHelpCallback.OnItemTouchCallbackListener;

public class TaskAdapter<T> extends Adapter<ViewHolder> {
    protected LayoutInflater inflater;
    protected Context mContext;
    protected List<T> mList;

    public TaskAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        mList = new ArrayList<>();
    }

    public void update(List<T> data) {
        mList.clear();
        if (data != null) {
            mList.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return mList;
    }

    public void add(T item) {
        mList.add(item);
        notifyItemInserted(mList.size() - 1);
    }

    public void remove(int pos) {
        if (pos >= mList.size()) {
            return;
        }

        mList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, mList.size() - pos);
    }

    public T get(int pos) {
        if (pos >= mList.size()) {
            return null;
        }

        return mList.get(pos);
    }

    @Override
    public int getItemCount() {
        if (null == mList) {
            return 0;
        }

        return mList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CardView cv = holder.itemView.findViewById(R.id.cv_content);
        cv.setOnClickListener(clickListener);
        cv.setTag(position);

        if (position >= mList.size()) {
            return;
        }

        LocationBean item = (LocationBean) mList.get(position);
        ((ViewHolder) holder).tvTitle.setText(item.getName());
        ((ViewHolder) holder).tvSubTitle.setText(item.getSubName());
    }

    private OnClickListener clickListener;

    public void setOnItemClickListener(OnClickListener listener) {
        clickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_route, parent, false));
    }

    public DefaultItemTouchHelpCallback getTouchHelpCallback() {
        return touchHelpCallback;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_subtitle)
        TextView tvSubTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private DefaultItemTouchHelpCallback touchHelpCallback = new DefaultItemTouchHelpCallback(new OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int pos) {
            remove(pos);
        }

        @Override
        public boolean onMove(int srcPos, int targetPos) {
            Collections.swap(mList, srcPos, targetPos);
            notifyItemMoved(srcPos, targetPos);
            return true;
        }
    });
}
