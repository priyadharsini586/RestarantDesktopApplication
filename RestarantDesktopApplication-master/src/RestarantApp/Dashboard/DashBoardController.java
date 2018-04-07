package RestarantApp.Dashboard;

import RestarantApp.Main;
import com.jfoenix.controls.JFXSnackbar;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

import javafx.stage.Stage;

import java.io.IOException;

public class DashBoardController  {

    public static final ObservableList names =
            FXCollections.observableArrayList();

    @FXML
    TreeView lstMenu;
    @FXML
    AnchorPane subCategory,rootPane;
    String root = "Root",monthly_report ="Monthly Report",category ="Category",item = "Items",add_category = "Add Category",view_category ="View Category"
            ,add_Item ="Add Item",list_item = "List Item",tax = "Tax",add_tax = "Add Tax",list_tax = "List Tax",combo = " Combo Items",add_combo = "Add Combo Items",
            combo_item = "Combo Details",table="Table",add_table="Add Table",list_table = "List Table";


    public void initialize() {

        TreeItem<String> treeItemRoot = new TreeItem<> (root);

        TreeItem<String> monthlyNode = new TreeItem<>(monthly_report);
        TreeItem<String> categoryNode = new TreeItem<>(category);
        TreeItem<String> itemNode = new TreeItem<>(item);
        TreeItem<String> taxNode = new TreeItem<>(tax);
        TreeItem<String> comboNode = new TreeItem<>(combo);
        TreeItem<String> tableNode = new TreeItem<>(table);
        treeItemRoot.getChildren().addAll(monthlyNode, categoryNode, itemNode,taxNode,comboNode,tableNode);

        TreeItem<String> nodeItemA1 = new TreeItem<>(add_category);
        TreeItem<String> nodeItemA2 = new TreeItem<>(view_category);
        categoryNode.getChildren().addAll(nodeItemA1, nodeItemA2);

        TreeItem<String> addItem = new TreeItem<>(add_Item);
        TreeItem<String> listItem = new TreeItem<>(list_item);
        itemNode.getChildren().addAll(addItem, listItem);

        TreeItem<String> addTax = new TreeItem<>(add_tax);
        TreeItem<String> listTax = new TreeItem<>(list_tax);
        taxNode.getChildren().addAll(addTax,listTax);

        TreeItem<String> addCombo = new TreeItem<>(add_combo);
        TreeItem<String> comboDetails = new TreeItem<>(combo_item);
        comboNode.getChildren().addAll(addCombo,comboDetails);

        TreeItem<String> addTable = new TreeItem<>(add_table);
        TreeItem<String> tableList = new TreeItem<>(list_table);
        tableNode.getChildren().addAll(addTable,tableList);

        treeItemRoot.setExpanded(true);
        lstMenu.setShowRoot(false);
        lstMenu.setRoot(treeItemRoot);

        lstMenu.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                System.out.println("Selected Text : " + selectedItem.getValue());
                if (selectedItem.getValue().equals(monthly_report))
                {

                }else if (selectedItem.getValue().equals(add_category))
                {
                    changePane("/RestarantApp/menuFxml/CategoryScene.fxml");
                }else if (selectedItem.getValue().equals(view_category))
                {
                    changePane("/RestarantApp/menuFxml/ViewCategoryScene.fxml");
                }else if (selectedItem.getValue().equals(add_Item))
                {
                    changePane("/RestarantApp/menuFxml/ItemScene.fxml");
                }else if (selectedItem.getValue().equals(list_item))
                {
                    changePane("/RestarantApp/menuFxml/item_list_controller.fxml");
                }else if (selectedItem.getValue().equals(add_tax))
                {
                    changePane("/RestarantApp/menuFxml/add_tax_scene.fxml");
                }else if (selectedItem.getValue().equals(list_tax))
                {
                    changePane("/RestarantApp/menuFxml/view_tax.fxml");
                }else if (selectedItem.getValue().equals(add_combo))
                {
                    changePane("/RestarantApp/menuFxml/comboscene.fxml");
                }else if (selectedItem.getValue().equals(combo_item))
                {
                    changePane("/RestarantApp/menuFxml/combo_list_scene.fxml");
                }else if (selectedItem.getValue().equals(add_table))
                {
                    changePane("/RestarantApp/menuFxml/add_table_scene.fxml");
                }else if (selectedItem.getValue().equals(list_table))
                {
                    changePane("/RestarantApp/menuFxml/table_list_scene.fxml");
                }
            }
        });

       /* names.addAll(
                "Monthly Report", "Category", "Items","Category List"
        );
        lstMenu.setItems(names);
        lstMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int menu = lstMenu.getSelectionModel().getSelectedIndex();
                if (menu == 1) {
                    changePane("/RestarantApp/menuFxml/CategoryScene.fxml");
                }else if (menu == 3)
                {
                    changePane("/RestarantApp/menuFxml/ViewCategoryScene.fxml");
                }

            }
        });*/
    }



    public void changePane(String urlPath)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(urlPath));
        StackPane cmdPane = null;
        try {
            cmdPane = (StackPane) loader.load();
            cmdPane.setAlignment(Pos.CENTER);
            cmdPane.setPrefWidth(subCategory.getWidth());
            cmdPane.setPrefHeight(subCategory.getHeight());


        } catch (IOException e) {
            e.printStackTrace();
        }
        subCategory.getChildren().setAll(cmdPane);
        System.out.println(subCategory.getWidth());
        System.out.println(subCategory.getHeight());

    }



}
