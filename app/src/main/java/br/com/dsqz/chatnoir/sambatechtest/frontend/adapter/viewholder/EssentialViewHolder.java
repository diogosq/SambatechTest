package br.com.dsqz.chatnoir.sambatechtest.frontend.adapter.viewholder;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;


@SuppressWarnings ("unused")
public class EssentialViewHolder extends RecyclerView.ViewHolder{
    
    private final View essentialView;
    
    public EssentialViewHolder(View view){
        super(view);
        essentialView = view;
    }
    
    public View getBaseView(){
        return essentialView;
    }
    
    public View getViewById(@IdRes int id){
        return essentialView.findViewById(id);
    }
    
    public void clearAnimation(@IdRes int id){
        essentialView.findViewById(id)
                     .clearAnimation();
    }
}
