package com.crash.customlist;

import java.util.ArrayList;

import com.crash.rettina.R;
import com.crash.rettina.RouteMenu;
import com.crash.routeinfo.Route;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class CustomAdapter extends BaseAdapter implements OnClickListener {
          
         /*********** Declare Used Variables *********/
         private Activity activity;
         private ArrayList<Route> data;
         public RouteMenu routeActivity;
         private static LayoutInflater inflater=null;
         public Resources res;
         Route tempValues=null;
         int i=0;
          
         /*************  CustomAdapter Constructor *****************/
         public CustomAdapter(Activity a, ArrayList<Route> d,Resources resLocal, RouteMenu r) {
              
                /********** Take passed values **********/
                 activity = a;
                 data=d;
                 res = resLocal;
                 routeActivity = r;

                 
                 /***********  Layout inflator to call external xml layout () ***********/
                  inflater = ( LayoutInflater )activity.
                                              getSystemService(Context.LAYOUT_INFLATER_SERVICE);
              
         }
      
         /******** What is the size of Passed Arraylist Size ************/
         public int getCount() {
              
             if(data.size()<=0)
                 return 1;
             return data.size();
         }
      
         public Object getItem(int position) {
             return position;
         }
      
         public long getItemId(int position) {
             return position;
         }
          
         /********* Create a holder Class to contain inflated xml file elements *********/
         public static class ViewHolder{
              
             public CheckBox cb_showRoute;
             public ImageButton imgbtn_removeRoute;
             public TextView tv_routeName;
      
         }
      
         /****** Depends upon data size called for each row , Create each ListView row *****/
         public View getView(int position, View convertView, ViewGroup parent) {
              
             View vi = convertView;
             ViewHolder holder;
              
             if(convertView==null){
                  
                 /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                 vi = inflater.inflate(R.layout.tabitem, null);
                  
                 /****** View Holder Object to contain tabitem.xml file elements ******/
 
                 holder = new ViewHolder();
                 holder.cb_showRoute = (CheckBox) vi.findViewById(R.id.cb_showroute);
                 holder.imgbtn_removeRoute = (ImageButton)vi.findViewById(R.id.imgbtn_removeroute);
                 holder.tv_routeName = (TextView)vi.findViewById(R.id.tv_routename);
                  
                /************  Set holder with LayoutInflater ************/
                 vi.setTag( holder );
             }
             else 
                 holder=(ViewHolder)vi.getTag();
              
             if(data.size()<=0)
             {
                 holder.tv_routeName.setText("No Data");
                  
             }
             else
             {
                 /***** Get each Model object from Arraylist ********/
                 tempValues=null;
                 tempValues = ( Route ) data.get( position );
                  
                 /************  Set Model values in Holder elements ***********/
 
                  holder.tv_routeName.setText(Integer.toString(tempValues.getRouteID()));
                  
//                  holder.text1.setText( tempValues.getUrl() );
//                   holder.image.setImageResource(
//                               res.getIdentifier(
//                               "com.androidexample.customlistview:drawable/"+tempValues.getImage()
//                               ,null,null));
                   
                  /******** Set Item Click Listner for LayoutInflater for each row *******/
 
                  vi.setOnClickListener(new OnItemClickListener( position ));
             }
             return vi;
         }
          
         @Override
         public void onClick(View v) {
                 Log.v("CustomAdapter", "=====Row button clicked=====");
         }
          
         /********* Called when Item click in ListView ************/
         private class OnItemClickListener  implements OnClickListener{           
             private int mPosition;
              
             OnItemClickListener(int position){
                  mPosition = position;
             }
              
             @Override
             public void onClick(View arg0) {
        
//               CustomListview sct = (CustomListview)activity;
            	 System.out.println("Clicked!!!");
            	 System.out.println("Route clicked: " + tempValues.getRouteID());
            	 //tempValues.printPoly();


            	 routeActivity.drawRoute(tempValues);
            	 
            	 
              /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/
 
//                 sct.onItemClick(mPosition);
             }               
         }   
     }