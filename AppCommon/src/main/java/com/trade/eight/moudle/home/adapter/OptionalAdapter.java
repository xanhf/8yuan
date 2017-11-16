package com.trade.eight.moudle.home.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.config.ProFormatConfig;
import com.trade.eight.entity.Optional;
import com.trade.eight.moudle.baksource.BakSourceInterface;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
public class OptionalAdapter extends BaseAdapter {

	private List<Optional> items = new ArrayList<Optional>();

	private Context context;

	private int count = 0;

	public DiffOrPercentChangeListener listener;

	private AnimationDrawable cusRedAnimationDrawable;

	private AnimationDrawable cusGreenAnimationDrawable;

	private HashMap<String, Double> getMap = new HashMap<String, Double>();

	public OptionalAdapter(Context context, List<Optional> list) {
		this.context = context;
		if (list != null) {
			items.addAll(list);
		}
	}

	public void clearGetMap (){
		getMap.clear();
	}
	public void setItems(List<Optional> list) {

		for (Optional optional : list) {
//            if (optional.isInitData()) {
				 //source+code
                getMap.put(ConvertUtil.NVL(optional.getExchangeID(), "") + optional.getInstrumentID(),
                        Double.parseDouble(optional.getLastPrice()));
//            }
		}
		items.clear();
		if (list != null) {
			items.addAll(list);
			notifyDataSetChanged();
		}
	}

	public int getOptionIndex(String codes) {
		for (Optional optional : items) {
			if (codes.equals(optional.getCode())) {
				return items.indexOf(optional);
			}
		}
		return 0;
	}

	public void clear () {
		items.clear();
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return items.size();

	}

	@Override
	public Optional getItem(int position) {
		return items.get(position);

	}

	@Override
	public long getItemId(int position) {
		return position;

	}

	public List<Optional> getItems() {
		return items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView;
		ViewHolder holder = null;
		if (view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.item_optional,
					parent, false);
			holder.symbol = (TextView) view.findViewById(R.id.symbol);
			holder.title = (TextView) view.findViewById(R.id.title);
//			holder.buyingRate = (TextView) view.findViewById(R.id.buying_rate);
			holder.sellingRate = (TextView) view
					.findViewById(R.id.selling_rate);
			holder.changeRate = (TextView) view.findViewById(R.id.change_rate);
			holder.llOptional = (LinearLayout) view
					.findViewById(R.id.ll_optional);
			holder.change_chengjiaoliang = (TextView) view.findViewById(R.id.change_chengjiaoliang);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

        try {
            Optional optional = items.get(position);
            if (BaseInterface.isBakSource) {
                if (optional.getType() != null && optional.getType().equalsIgnoreCase(BakSourceInterface.TRUDE_SOURCE_WGJS)) {
                    holder.title.setText(optional.getName() + "(国际)");
                } else {
					if(!TextUtils.isEmpty(optional.getName())){
						holder.title.setText(optional.getName());
					}
				}

			} else {
				if(!TextUtils.isEmpty(optional.getName())){
					holder.title.setText(optional.getName());
				}
            }

            holder.symbol.setText(optional.getInstrumentID());

			double diff = Double.parseDouble(optional.getChange());
                holder.sellingRate.setText(optional.getLastPrice());

				checkChange(holder.sellingRate, ConvertUtil.NVL(optional.getExchangeID(), "") + optional.getInstrumentID(),
						Double.parseDouble(optional.getLastPrice()));
				String prefix = "";
                try {
                    if (diff > 0) {
						prefix = "+";
						holder.sellingRate.setTextColor(context.getResources().getColor(R.color.color_opt_gt));
						holder.changeRate.setTextColor(context.getResources().getColor(R.color.color_opt_gt));
//						holder.changeRate.setBackgroundResource(R.drawable.opt_zhangdiefu_bg_gt);
                    } else if (diff == 0) {
						holder.sellingRate.setTextColor(context.getResources().getColor(R.color.color_opt_eq));
						holder.changeRate.setTextColor(context.getResources().getColor(R.color.color_opt_eq));
//						holder.changeRate.setBackgroundResource(R.drawable.opt_zhangdiefu_bg_eq);
                    } else {
						holder.sellingRate.setTextColor(context.getResources().getColor(R.color.color_opt_lt));
						holder.changeRate.setTextColor(context.getResources().getColor(R.color.color_opt_lt));
//						holder.changeRate.setBackgroundResource(R.drawable.opt_zhangdiefu_bg_lt);
                    }
                }catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
                switch (count % 2) {
                    case 0:
                        holder.changeRate.setText(prefix + optional.getChg());
                        break;
                    case 1:
						holder.changeRate.setText(prefix + NumberUtil.moveLast0(diff));
						break;

                    default:
                        break;
                }
			holder.change_chengjiaoliang.setText(optional.getVolume());
            holder.changeRate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    count++;
                    if (listener != null)
                        listener.change(count);
                    notifyDataSetChanged();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
	}

	public void updateItem(TextView sellingRate,TextView changeRate,TextView change_chengjiaoliang,Optional optional){
		double diff = Double.parseDouble(optional.getChange());
		 sellingRate.setText(optional.getLastPrice());

		checkChange( sellingRate, ConvertUtil.NVL(optional.getExchangeID(), "") + optional.getInstrumentID(),
				Double.parseDouble(optional.getLastPrice()));
		String prefix = "";
		try {
			if (diff > 0) {
				prefix = "+";
				 sellingRate.setTextColor(context.getResources().getColor(R.color.color_opt_gt));
				 changeRate.setTextColor(context.getResources().getColor(R.color.color_opt_gt));
//						 changeRate.setBackgroundResource(R.drawable.opt_zhangdiefu_bg_gt);
			} else if (diff == 0) {
				 sellingRate.setTextColor(context.getResources().getColor(R.color.color_opt_eq));
				 changeRate.setTextColor(context.getResources().getColor(R.color.color_opt_eq));
//						 changeRate.setBackgroundResource(R.drawable.opt_zhangdiefu_bg_eq);
			} else {
				 sellingRate.setTextColor(context.getResources().getColor(R.color.color_opt_lt));
				 changeRate.setTextColor(context.getResources().getColor(R.color.color_opt_lt));
//						 changeRate.setBackgroundResource(R.drawable.opt_zhangdiefu_bg_lt);
			}
		}catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		switch (count % 2) {
			case 0:
				 changeRate.setText(prefix + optional.getChg());
				break;
			case 1:
				 changeRate.setText(prefix + NumberUtil.moveLast0(diff));
				break;

			default:
				break;
		}
		 change_chengjiaoliang.setText(optional.getVolume());
	}

	private void checkChange(View v, String getTreaty, Double Sellone) {

		Iterator<?> iter = getMap.entrySet().iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();

			if (key.equals(getTreaty)) {

				if (val == null) {

				} else {
					if (Sellone > (Double) val) {
						v.setBackgroundResource(R.drawable.shansuo_red);
						cusRedAnimationDrawable = (AnimationDrawable) v
								.getBackground();
						cusRedAnimationDrawable.stop();
						cusRedAnimationDrawable.start();
						getMap.put(getTreaty, Sellone);

					} else if (Sellone < (Double) val) {
						v.setBackgroundResource(R.drawable.shansuo_green);
						cusGreenAnimationDrawable = (AnimationDrawable) v
								.getBackground();
						cusGreenAnimationDrawable.stop();
						cusGreenAnimationDrawable.start();
						getMap.put(getTreaty, Sellone);
					}
				}
				break;
			}
		}

	}

	class ViewHolder {
		TextView title;
		TextView symbol;
//		TextView buyingRate;
		TextView sellingRate;
		TextView changeRate;
		LinearLayout llOptional;
		TextView change_chengjiaoliang;
	}

	public interface DiffOrPercentChangeListener {
		void change(int count);
	}

}
