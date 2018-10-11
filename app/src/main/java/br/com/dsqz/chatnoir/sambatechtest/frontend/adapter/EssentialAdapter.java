package br.com.dsqz.chatnoir.sambatechtest.frontend.adapter;

import android.content.Context;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

import br.com.dsqz.chatnoir.sambatechtest.frontend.adapter.viewholder.EssentialViewHolder;

@SuppressWarnings ( {"WeakerAccess", "unused"})
public class EssentialAdapter<T> extends RecyclerView.Adapter<EssentialViewHolder>{
    
    protected final Context context;
    
    private final int                  layout;
    private final int                  animationTableViewRes;
    private final int                  animationRes;
    private       int                  lastShownItem;
    private       View                 animationTableView;
    private       Animation            animation;
    private       View.OnClickListener onClickListener;
    private       AdapterViewBinder<T> viewBinder;
    private       List<T>              mEssentialList;
    
    
    public EssentialAdapter(Context context, @LayoutRes int layout, List<T> essentialList){
        this(context, layout, null, essentialList);
    }
    
    public EssentialAdapter(Context context, @LayoutRes int layout, @Nullable AdapterViewBinder<T> viewBinder, List<T> essentialList){
        this(context, layout, viewBinder, 0, 0, essentialList);
    }
    
    public EssentialAdapter(Context context, @LayoutRes int layout, @Nullable AdapterViewBinder<T> viewBinder,
                            @IdRes int animationTableView, @AnimRes int animation, List<T> essentialList){
        this.context = context;
        this.layout = layout;
        this.mEssentialList = essentialList;
        this.viewBinder = viewBinder;
        this.animationTableViewRes = animationTableView;
        this.animationRes = animation;
        this.lastShownItem = -1;
    }
    
    public View.OnClickListener getOnClickListener(){
        return onClickListener;
    }
    
    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
    
    public List<T> getObjects(){
        return mEssentialList;
    }
    
    public void setObjects(List<T> objects){
        this.mEssentialList = objects;
        notifyDataSetChanged();
        lastShownItem = -1;
    }
    
    @SafeVarargs
    public final void addObjects(EnumsHelper.ListPosition position, T... items){
        for(T item : items){
            addObject(position, item);
        }
    }
    
    public void addObjects(EnumsHelper.ListPosition position, ArrayList<T> items){
        for(T item : items){
            addObject(position, item);
        }
    }
    
    public void addObject(EnumsHelper.ListPosition position, T item){
        if(mEssentialList.contains(item)) return;
        switch(position){
            case TOP:
                this.mEssentialList.add(0, item);
                this.notifyItemInserted(0);
                break;
            case BOTTOM:
                this.mEssentialList.add(item);
                this.notifyItemInserted(this.mEssentialList.size() - 1);
                break;
        }
    }
    
    public boolean removeObjectAtPosition(int position){
        if(position >= mEssentialList.size()) return false;
        
        mEssentialList.remove(position);
        this.notifyItemRemoved(position);
        return true;
    }
    
    private void setAnimation(int position){
        if(animationTableView != null && animation != null){
            if(position > lastShownItem){
                animationTableView.startAnimation(animation);
                lastShownItem = position;
            }
        }
    }
    
    public void setAnimationTableView(EssentialViewHolder holder, @IdRes int viewId, @AnimRes int animationId){
        animation = AnimationUtils.loadAnimation(context, animationId);
        animationTableView = holder.getViewById(viewId);
    }
    
    public T getItem(int position){
        return mEssentialList.get(position);
    }
    
    @Override
    public int getItemCount(){
        return mEssentialList.size();
    }
    
    @Override
    public long getItemId(int position){
        return position;
    }
    
    public EssentialAdapter<T> setViewBinder(AdapterViewBinder<T> viewBinder){
        this.viewBinder = viewBinder;
        return this;
    }
    
    @NonNull
    @Override
    public EssentialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        
        View convertView = layoutInflater.inflate(layout, parent, false);
        convertView.setTag(new EssentialViewHolder(convertView));
        
        return (EssentialViewHolder) convertView.getTag();
    }
    
    @Override
    public void onBindViewHolder(@NonNull EssentialViewHolder holder, int position){
        
        if(animationTableViewRes != 0 && animationRes != 0){
            setAnimationTableView(holder, animationTableViewRes, animationRes);
            setAnimation(position);
        }
        
        if(viewBinder != null) viewBinder.bind(holder, getItem(position), position);
        
        if(onClickListener != null) holder.getBaseView()
                                          .setOnClickListener(onClickListener);
    }
    
    public interface AdapterViewBinder<T>{
        void bind(EssentialViewHolder holder, T item, int position);
    }
    
    public static class EnumsHelper{
        public enum ListPosition{
            BOTTOM,
            TOP
        }
    }
}

