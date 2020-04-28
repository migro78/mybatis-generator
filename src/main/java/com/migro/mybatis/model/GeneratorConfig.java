package com.migro.mybatis.model;

/**
 *
 * GeneratorConfig is the Config of mybatis generator config exclude database
 * config
 *
 * Created by Owen on 6/16/16.
 */
public class GeneratorConfig {

	/**
	 * 本配置的名称
	 */
	private String name;

	private String connectorJarPath;

	private String projectFolder;

	private String tableNameField;

	private String entityPackage;

	private String mapperPackage;

	private String servicePackage;

	private String serviceImplPackage;

	private String controllerPackage;

	private String entityParent;

	private String mapperParent;

	private String serviceParent;

	private String serviceImplParent;

	private String controllerParent;

	private String basePackage;

	private String author;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConnectorJarPath() {
		return connectorJarPath;
	}

	public void setConnectorJarPath(String connectorJarPath) {
		this.connectorJarPath = connectorJarPath;
	}

	public String getProjectFolder() {
		return projectFolder;
	}

	public void setProjectFolder(String projectFolder) {
		this.projectFolder = projectFolder;
	}

	public String getTableNameField() {
		return tableNameField;
	}

	public void setTableNameField(String tableNameField) {
		this.tableNameField = tableNameField;
	}

	public String getEntityPackage() {
		return entityPackage;
	}

	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}

	public String getMapperPackage() {
		return mapperPackage;
	}

	public void setMapperPackage(String mapperPackage) {
		this.mapperPackage = mapperPackage;
	}

	public String getServicePackage() {
		return servicePackage;
	}

	public void setServicePackage(String servicePackage) {
		this.servicePackage = servicePackage;
	}

	public String getServiceImplPackage() {
		return serviceImplPackage;
	}

	public void setServiceImplPackage(String serviceImplPackage) {
		this.serviceImplPackage = serviceImplPackage;
	}

	public String getControllerPackage() {
		return controllerPackage;
	}

	public void setControllerPackage(String controllerPackage) {
		this.controllerPackage = controllerPackage;
	}

	public String getEntityParent() {
		return entityParent;
	}

	public void setEntityParent(String entityParent) {
		this.entityParent = entityParent;
	}

	public String getMapperParent() {
		return mapperParent;
	}

	public void setMapperParent(String mapperParent) {
		this.mapperParent = mapperParent;
	}

	public String getServiceParent() {
		return serviceParent;
	}

	public void setServiceParent(String serviceParent) {
		this.serviceParent = serviceParent;
	}

	public String getServiceImplParent() {
		return serviceImplParent;
	}

	public void setServiceImplParent(String serviceImplParent) {
		this.serviceImplParent = serviceImplParent;
	}

	public String getControllerParent() {
		return controllerParent;
	}

	public void setControllerParent(String controllerParent) {
		this.controllerParent = controllerParent;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
