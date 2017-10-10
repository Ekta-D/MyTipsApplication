package com.mytips.Interface;

import com.mytips.Model.TipeeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Beesolver on 10-10-2017.
 */

public interface TipeeSelected {

     TipeeInfo TipeeCheckedList(int position,boolean isChecked, TipeeInfo selected_tipee,List<String> selected_tipeeslist);
}
