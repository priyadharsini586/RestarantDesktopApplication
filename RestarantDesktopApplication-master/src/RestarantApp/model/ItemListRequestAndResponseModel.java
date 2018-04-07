package RestarantApp.model;

import java.util.ArrayList;
import java.util.List;

public class ItemListRequestAndResponseModel {
    String status_code,itemId,itemName,itemDescription,itemImage,itemPrice,itemShortCode,id,name,status,value,combine,comboList;
    int tot_items,tot_taxes;
    ArrayList<item_list>item_list = new ArrayList<>();
    ArrayList<list>list = new ArrayList<>();

    String itemCategoryList,Status_code,Success ;


    public String getComboList() {
        return comboList;
    }

    public void setComboList(String comboList) {
        this.comboList = comboList;
    }

    public String getCombine() {
        return combine;
    }

    public void setCombine(String combine) {
        this.combine = combine;
    }

    public ArrayList<list> getTax_list() {
        return list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<ItemListRequestAndResponseModel.list> getList() {
        return list;
    }

    public void setList(ArrayList<ItemListRequestAndResponseModel.list> list) {
        this.list = list;
    }

    public void setTax_list(ArrayList<list> tax_list) {
        this.list = tax_list;
    }

    public int getTot_taxes() {
        return tot_taxes;
    }

    public void setTot_taxes(int tot_taxes) {
        this.tot_taxes = tot_taxes;
    }

    public String getSuccess() {
        return Success;
    }

    public void setSuccess(String success) {
        Success = success;
    }

    public void setStatusCode(String code)
    {
        this.Status_code = code;

    }
    public String getStatusCode() {
        return Success;
    }


    public String getItemShortCode() {
        return itemShortCode;
    }

    public void setItemShortCode(String itemShortCode) {
        this.itemShortCode = itemShortCode;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public ArrayList<ItemListRequestAndResponseModel.item_list> getItem_list() {
        return item_list;
    }

    public void setItem_list(ArrayList<ItemListRequestAndResponseModel.item_list> item_list) {
        this.item_list = item_list;
    }

    public String getItemCategoryList() {
        return itemCategoryList;
    }

    public void setItemCategoryList(String itemCategoryList) {
        this.itemCategoryList = itemCategoryList;
    }

    public int getTot_items() {
        return tot_items;
    }

    public void setTot_items(int tot_items) {
        this.tot_items = tot_items;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public ArrayList<item_list> getItem_lists() {
        return item_list;
    }

    public void setItem_lists(ArrayList<item_list> item_lists) {
        this.item_list = item_lists;
    }

    public class item_list
    {
        String item_id,item_name,description,price,image,short_code;
        List<cat_list> cat_list;

        public String getShort_code() {
            return short_code;
        }

        public void setShort_code(String short_code) {
            this.short_code = short_code;
        }

        public String getItem_id() {
            return item_id;
        }

        public void setItem_id(String item_id) {
            this.item_id = item_id;
        }

        public String getItem_name() {
            return item_name;
        }

        public void setItem_name(String item_name) {
            this.item_name = item_name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public List<cat_list> getCat_list() {
            return cat_list;
        }

        public void setCat_list(List<cat_list> cat_list) {
            this.cat_list = cat_list;
        }
    }

    public class cat_list
    {
        String cat_id,cat_name;

        public String getCat_id() {
            return cat_id;
        }

        public void setCat_id(String cat_id) {
            this.cat_id = cat_id;
        }

        public String getCat_name() {
            return cat_name;
        }

        public void setCat_name(String cat_name) {
            this.cat_name = cat_name;
        }
    }

    public class list{
        String id,name,value,comp1,comp2,active,item_list;

        public String getItem_list() {
            return item_list;
        }

        public void setItem_list(String item_list) {
            this.item_list = item_list;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getComp1() {
            return comp1;
        }

        public void setComp1(String comp1) {
            this.comp1 = comp1;
        }

        public String getComp2() {
            return comp2;
        }

        public void setComp2(String comp2) {
            this.comp2 = comp2;
        }

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }
    }

}
