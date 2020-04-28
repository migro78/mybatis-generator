package com.migro.mybatis.controller;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.migro.mybatis.enums.FXMLPage;
import com.migro.mybatis.model.DatabaseConfig;
import com.migro.mybatis.model.GeneratorConfig;
import com.migro.mybatis.util.AlertUtil;
import com.migro.mybatis.util.ConfigHelper;
import com.migro.mybatis.util.DbUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.sql.SQLRecoverableException;
import java.util.*;
import java.util.List;

/**
 * <p>
 * 类描述
 * </p>
 *
 * @author migro
 * @since 2020/2/15 13:53
 */
public class MainUIController extends BaseFXController {
    protected Logger logger = LogManager.getLogger();
    private static final String FOLDER_NO_EXIST = "选择的目录不存在，是否创建？";
    @FXML
    private Label connectionLabel;
    @FXML
    private Label configsLabel;
    @FXML
    private TreeView<String> leftDBTree;
    @FXML
    private TextField tableNameField;
    @FXML
    private TextField projectFolderField;
    @FXML
    private TextField author;
    @FXML
    private TextField currentDb;
    /**
     * 包名输入框
     */
    @FXML
    private TextField basePackage;
    @FXML
    private TextField entityPackage;
    @FXML
    private TextField mapperPackage;
    @FXML
    private TextField servicePackage;
    @FXML
    private TextField serviceImplPackage;
    @FXML
    private TextField controllerPackage;
    /**
     * 父类输入框
     */
    @FXML
    private TextField entityParent;
    @FXML
    private TextField mapperParent;
    @FXML
    private TextField serviceParent;
    @FXML
    private TextField serviceImplParent;
    @FXML
    private TextField controllerParent;


    private DatabaseConfig selectedDatabaseConfig;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImageView dbImage = new ImageView("icons/computer.png");
        dbImage.setFitHeight(40);
        dbImage.setFitWidth(40);
        connectionLabel.setGraphic(dbImage);
        connectionLabel.setOnMouseClicked(event -> {
            DbConnectionController controller = (DbConnectionController) loadFXMLPage("新建数据库连接", FXMLPage.NEW_CONNECTION, false);
            controller.setMainUIController(this);
            controller.showDialogStage();
        });
        ImageView configImage = new ImageView("icons/config-list.png");
        configImage.setFitHeight(40);
        configImage.setFitWidth(40);
        configsLabel.setGraphic(configImage);
        configsLabel.setOnMouseClicked(event -> {
            GeneratorConfigController controller = (GeneratorConfigController) loadFXMLPage("配置", FXMLPage.GENERATOR_CONFIG, false);
            controller.setMainUIController(this);
            controller.showDialogStage();
        });

        leftDBTree.setShowRoot(false);
        leftDBTree.setRoot(new TreeItem<>());
        Callback<TreeView<String>, TreeCell<String>> defaultCellFactory = TextFieldTreeCell.forTreeView();
        leftDBTree.setCellFactory((TreeView<String> tv) -> {
            TreeCell<String> cell = defaultCellFactory.call(tv);
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                int level = leftDBTree.getTreeItemLevel(cell.getTreeItem());
                TreeCell<String> treeCell = (TreeCell<String>) event.getSource();
                TreeItem<String> treeItem = treeCell.getTreeItem();
                if (level == 1) {
                    final ContextMenu contextMenu = new ContextMenu();
                    MenuItem item1 = new MenuItem("关闭连接");
                    item1.setOnAction(event1 -> treeItem.getChildren().clear());
                    MenuItem item2 = new MenuItem("编辑连接");
                    item2.setOnAction(event1 -> {
                        DatabaseConfig selectedConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
                        DbConnectionController controller = (DbConnectionController) loadFXMLPage("编辑数据库连接", FXMLPage.NEW_CONNECTION, false);
                        controller.setMainUIController(this);
                        controller.setConfig(selectedConfig);
                        controller.showDialogStage();
                    });
                    MenuItem item3 = new MenuItem("删除连接");
                    item3.setOnAction(event1 -> {
                        DatabaseConfig selectedConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
                        try {
                            ConfigHelper.deleteDatabaseConfig(selectedConfig);
                            this.loadLeftDBTree();
                        } catch (Exception e) {
                            AlertUtil.showErrorAlert("Delete connection failed! Reason: " + e.getMessage());
                        }
                    });
                    contextMenu.getItems().addAll(item1, item2, item3);
                    cell.setContextMenu(contextMenu);
                } else if(level == 2){
                    // 选中表
                    List<TreeItem<String>> items = leftDBTree.getSelectionModel().getSelectedItems();
                    String selectTables = "";
                    for(TreeItem<String> it:items){
                        selectTables = selectTables + "," + it.getValue() ;
                    }
                    if(selectTables.length()>0){
                        selectTables = selectTables.substring(1);
                    }
                    tableNameField.setText(selectTables);
                }
                if (event.getClickCount() == 2) {
                    if (treeItem == null) {
                        return;
                    }
                    treeItem.setExpanded(true);
                    if (level == 1) {
                        System.out.println("index: " + leftDBTree.getSelectionModel().getSelectedIndex());
                        selectedDatabaseConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
                        System.out.println("selectedDatabaseConfig: " + selectedDatabaseConfig);
                        currentDb.setText(selectedDatabaseConfig.getName());
                        try {
                            List<String> tables = DbUtil.getTableNames(selectedDatabaseConfig);
                            if (tables != null && tables.size() > 0) {
                                ObservableList<TreeItem<String>> children = cell.getTreeItem().getChildren();
                                children.clear();
                                for (String tableName : tables) {
                                    TreeItem<String> newTreeItem = new TreeItem<>();
                                    ImageView imageView = new ImageView("icons/table.png");
                                    imageView.setFitHeight(16);
                                    imageView.setFitWidth(16);
                                    newTreeItem.setGraphic(imageView);
                                    newTreeItem.setValue(tableName);
                                    children.add(newTreeItem);
                                }
                            }
                        } catch (SQLRecoverableException e) {
                            logger.error(e.getMessage(), e);
                            AlertUtil.showErrorAlert("连接超时");
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            AlertUtil.showErrorAlert(e.getMessage());
                        }

                    }
                }
            });
            return cell;
        });
        loadLeftDBTree();
        setTooltip();
        //默认选中第一个，否则如果忘记选择，没有对应错误提示
        //encodingChoice.getSelectionModel().selectFirst();
        leftDBTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    void loadLeftDBTree() {
        TreeItem rootTreeItem = leftDBTree.getRoot();
        rootTreeItem.getChildren().clear();
        try {
            List<DatabaseConfig> dbConfigs = ConfigHelper.loadDatabaseConfig();
            for (DatabaseConfig dbConfig : dbConfigs) {
                TreeItem<String> treeItem = new TreeItem<>();
                treeItem.setValue(dbConfig.getName());
                ImageView dbImage = new ImageView("icons/computer.png");
                dbImage.setFitHeight(16);
                dbImage.setFitWidth(16);
                dbImage.setUserData(dbConfig);
                treeItem.setGraphic(dbImage);
                rootTreeItem.getChildren().add(treeItem);
            }
        } catch (Exception e) {
            logger.error("connect db failed, reason: {}", e);
            AlertUtil.showErrorAlert(e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
        }
    }

    @FXML
    public void chooseProjectFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedFolder = directoryChooser.showDialog(getPrimaryStage());
        if (selectedFolder != null) {
            projectFolderField.setText(selectedFolder.getAbsolutePath());
        }
    }

    /**
     * 生成代码
     *
     * @return
     */
    @FXML
    public void generateCode() {
        if (StringUtils.isEmpty(tableNameField.getText())) {
            AlertUtil.showWarnAlert("请先在左侧选择数据库表");
            return;
        }
        String result = validateConfig();
        if (result != null) {
            AlertUtil.showErrorAlert(result);
            return;
        }
        GeneratorConfig generatorConfig = getGeneratorConfigFromUI();
        if (!checkDirs(generatorConfig)) {
            return;
        }

        try {
            doGenerateCode(generatorConfig,selectedDatabaseConfig);
            AlertUtil.showInfoAlert("代码创建成功！");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }

    private void doGenerateCode(GeneratorConfig generatorConfig,DatabaseConfig databaseConfig) throws Exception{
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        // 保存路径
        gc.setOutputDir(generatorConfig.getProjectFolder());
        gc.setFileOverride(true);
        gc.setActiveRecord(false);
        // XML 二级缓存
        gc.setEnableCache(true);
        // XML ResultMap
        gc.setBaseResultMap(false);
        // XML columList
        gc.setBaseColumnList(false);
        gc.setOpen(false);
        gc.setAuthor(generatorConfig.getAuthor());
        mpg.setGlobalConfig(gc);
        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(getMybatisDbType(databaseConfig.getDbType()));
        com.migro.mybatis.model.DbType dbType = com.migro.mybatis.model.DbType.valueOf(databaseConfig.getDbType());
        dsc.setDriverName(dbType.getDriverClass());
        dsc.setUsername(databaseConfig.getUsername());
        dsc.setPassword(databaseConfig.getPassword());
        dsc.setSchemaName("public");
        dsc.setUrl(DbUtil.getConnectionUrlWithSchema(databaseConfig));
        mpg.setDataSource(dsc);
        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        //strategy.setTablePrefix("t_");// 此处可以修改为您的表前缀
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 需要生成的表
        String[] tables = tableNameField.getText().split(",");
        strategy.setInclude(tables);
        // strategy.setExclude(new String[]{"test"}); // 排除生成的表
        // 自定义实体父类
        strategy.setSuperEntityClass(generatorConfig.getEntityParent());
        // 自定义实体，公共字段
        strategy.setSuperEntityColumns( new String[]{"id"});
        // 自定义 mapper 父类
        strategy.setSuperMapperClass(generatorConfig.getMapperParent());
        // 自定义 service 父类
        strategy.setSuperServiceClass(generatorConfig.getServiceParent());
        // 自定义 service 实现类父类
        strategy.setSuperServiceImplClass(generatorConfig.getServiceImplParent());
        // 自定义 controller 父类
        strategy.setSuperControllerClass(generatorConfig.getControllerParent());
        strategy.setLogicDeleteFieldName("enable");
        mpg.setStrategy(strategy);
        // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("rpcService", false);
                this.setMap(map);
            }
        };
        mpg.setCfg(cfg);
        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(generatorConfig.getBasePackage());
        pc.setEntity(generatorConfig.getEntityPackage());
        pc.setMapper(generatorConfig.getMapperPackage());
        pc.setXml(generatorConfig.getMapperPackage());
        pc.setService(generatorConfig.getServicePackage());
        pc.setServiceImpl(generatorConfig.getServiceImplPackage());
        pc.setController(generatorConfig.getControllerPackage());
        mpg.setPackageInfo(pc);
        // 放置自己项目的 src/main/resources/template 目录下, 默认名称一下可以不配置，也可以自定义模板名称
        TemplateConfig tc = new TemplateConfig();
        tc.setEntity("tpl/entity.java.vm");
        tc.setMapper("tpl/mapper.java.vm");
        tc.setXml("tpl/mapper.xml.vm");
        tc.setService("tpl/iservice.java.vm");
        tc.setServiceImpl("tpl/serviceImpl.java.vm");
        tc.setController("tpl/controller.java.vm");
        mpg.setTemplate(tc);
        // 执行生成
        mpg.execute();
    }

    private DbType getMybatisDbType(String dbName){
        DbType ret = null;
        if("PostgreSQL".equals(dbName)){
            ret = DbType.POSTGRE_SQL;
        }
        return ret;
    }

    /**
     * 保存配置
     *
     * @return
     */
    @FXML
    public void saveGeneratorConfig(){
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("保存当前配置");
        dialog.setContentText("请输入配置名称");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String name = result.get();
            if (StringUtils.isEmpty(name)) {
                AlertUtil.showErrorAlert("名称不能为空");
                return;
            }
            logger.info("user choose name: {}", name);
            try {
                GeneratorConfig generatorConfig = getGeneratorConfigFromUI();
                generatorConfig.setName(name);
                ConfigHelper.deleteGeneratorConfig(name);
                ConfigHelper.saveGeneratorConfig(generatorConfig);
            } catch (Exception e) {
                AlertUtil.showErrorAlert("保存配置失败");
            }
        }
    }

    @FXML
    public void openTargetFolder(){
        GeneratorConfig generatorConfig = getGeneratorConfigFromUI();
        String projectFolder = generatorConfig.getProjectFolder();
        try {
            Desktop.getDesktop().browse(new File(projectFolder).toURI());
        }catch (Exception e) {
            AlertUtil.showErrorAlert("打开目录失败，请检查目录是否填写正确" + e.getMessage());
        }
    }

    private String validateConfig() {
        String projectFolder = projectFolderField.getText();
        if (StringUtils.isEmpty(projectFolder)) {
            return "保存目录不能为空";
        }
        if(StringUtils.isEmpty(author.getText())){
            return "author不能为空";
        }
        if (StringUtils.isAnyEmpty(basePackage.getText(), entityPackage.getText(), mapperPackage.getText(), servicePackage.getText(), serviceImplPackage.getText()
                , controllerPackage.getText())) {
            return "包名不能为空";
        }

        return null;
    }

    /**
     * 获取页面上配置参数
     *
     * @return
     */
    public GeneratorConfig getGeneratorConfigFromUI() {
        GeneratorConfig generatorConfig = new GeneratorConfig();
        generatorConfig.setProjectFolder(projectFolderField.getText());
        generatorConfig.setBasePackage(basePackage.getText());
        generatorConfig.setEntityPackage(entityPackage.getText());
        generatorConfig.setMapperPackage(mapperPackage.getText());
        generatorConfig.setServicePackage(servicePackage.getText());
        generatorConfig.setServiceImplPackage(serviceImplPackage.getText());
        generatorConfig.setControllerPackage(controllerPackage.getText());
        generatorConfig.setEntityParent(entityParent.getText());
        generatorConfig.setMapperParent(mapperParent.getText());
        generatorConfig.setTableNameField(tableNameField.getText());
        generatorConfig.setServiceParent(serviceParent.getText());
        generatorConfig.setServiceImplParent(serviceImplParent.getText());
        generatorConfig.setControllerParent(controllerParent.getText());
        generatorConfig.setAuthor(author.getText());
        return generatorConfig;
    }


    /**
     * 检查并创建不存在的文件夹
     *
     * @return
     */
    private boolean checkDirs(GeneratorConfig config) {
        List<String> dirs = new ArrayList<>();
        dirs.add(config.getProjectFolder());
        boolean haveNotExistFolder = false;
        for (String dir : dirs) {
            File file = new File(dir);
            if (!file.exists()) {
                haveNotExistFolder = true;
            }
        }
        if (haveNotExistFolder) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText(FOLDER_NO_EXIST);
            Optional<ButtonType> optional = alert.showAndWait();
            if (optional.isPresent()) {
                if (ButtonType.OK == optional.get()) {
                    try {
                        for (String dir : dirs) {
                            FileUtils.forceMkdir(new File(dir));
                        }
                        return true;
                    } catch (Exception e) {
                        AlertUtil.showErrorAlert("创建目录失败，请检查目录是否是文件而非目录");
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private void setTooltip() {
//        encodingChoice.setTooltip(new Tooltip("生成文件的编码，必选"));
//        generateKeysField.setTooltip(new Tooltip("insert时可以返回主键ID"));
//        offsetLimitCheckBox.setTooltip(new Tooltip("是否要生成分页查询代码"));
//        commentCheckBox.setTooltip(new Tooltip("使用数据库的列注释作为实体类字段名的Java注释 "));
//        useActualColumnNamesCheckbox.setTooltip(new Tooltip("是否使用数据库实际的列名作为实体类域的名称"));
//        useTableNameAliasCheckbox.setTooltip(new Tooltip("在Mapper XML文件中表名使用别名，并且列全部使用as查询"));
//        overrideXML.setTooltip(new Tooltip("重新生成时把原XML文件覆盖，否则是追加"));
//        useDAOExtendStyle.setTooltip(new Tooltip("将通用接口方法放在公共接口中，DAO接口留空"));
//        forUpdateCheckBox.setTooltip(new Tooltip("在Select语句中增加for update后缀"));
    }

    public void setGeneratorConfigIntoUI(GeneratorConfig generatorConfig) {
        projectFolderField.setText(generatorConfig.getProjectFolder());
        basePackage.setText(generatorConfig.getBasePackage());
        entityPackage.setText(generatorConfig.getEntityPackage());
        mapperPackage.setText(generatorConfig.getMapperPackage());
        servicePackage.setText(generatorConfig.getServicePackage());
        serviceImplPackage.setText(generatorConfig.getServiceImplPackage());
        controllerPackage.setText(generatorConfig.getControllerPackage());
        entityParent.setText(generatorConfig.getEntityParent());
        mapperParent.setText(generatorConfig.getMapperParent());
        serviceParent.setText(generatorConfig.getServiceParent());
        serviceImplParent.setText(generatorConfig.getServiceImplParent());
        controllerParent.setText(generatorConfig.getControllerParent());
        author.setText(generatorConfig.getAuthor());

    }
}
