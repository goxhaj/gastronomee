# Gastronomee application steps

## Step 1 - Generation of jhipster (contents of .yo-rc.json file)

{
  "generator-jhipster": {
    "promptValues": {
      "packageName": "com.gastronomee",
      "nativeLanguage": "en"
    },
    "jhipsterVersion": "4.3.0",
    "baseName": "gastronomee",
    "packageName": "com.gastronomee",
    "packageFolder": "com/gastronomee",
    "serverPort": "8080",
    "authenticationType": "session",
    "hibernateCache": "ehcache",
    "clusteredHttpSession": false,
    "websocket": "spring-websocket",
    "databaseType": "sql",
    "devDatabaseType": "h2Disk",
    "prodDatabaseType": "postgresql",
    "searchEngine": "elasticsearch",
    "messageBroker": false,
    "serviceDiscoveryType": false,
    "buildTool": "maven",
    "enableSocialSignIn": true,
    "rememberMeKey": "58b836494e88c1b6d65c556851b557cc76a06eaa",
    "clientFramework": "angular1",
    "useSass": false,
    "clientPackageManager": "yarn",
    "applicationType": "monolith",
    "testFrameworks": [
      "gatling",
      "cucumber",
      "protractor"
    ],
    "jhiPrefix": "jhi",
    "enableTranslation": true,
    "nativeLanguage": "en",
    "languages": [
      "en"
    ]
  }
}

## Step 2 - Adding a new role 

### Step 2.1 - Create a file on: src/main/resources/config/liquibase/changelog/loadData.xml with contents:

<?xml version="1.0" encoding="utf-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.5.xsd">

	<changeSet author="ardi" id="load_manager">
	    <insert tableName="jhi_authority ">
	        <column name="name" value="ROLE_MANAGER"/>
	    </insert>
	</changeSet>

</databaseChangeLog>


which basically adds a new role: ROLE_MANAGER to the jhi_authority table

### Step 2.2 - Add a reference to this new file created in src/main/resources/config/liquibase/master.xml 

    <!-- load application specific data here -->
    <include file="classpath:config/liquibase/changelog/load_data.xml" relativeToChangelogFile="false"/>
    

### Step 2.3 - Add the role into the registration form, change the file: /home/ardi/Documents/jhipster/gastronomee/src/main/webapp/app/account/register/register.html after username add:

    <div class="form-group">
		            <label data-translate="register.roles">Roles</label>
		            <select class="form-control" name="authority" ng-model="vm.authority"
		                    ng-options="authority for authority in vm.authorities">		               
		            </select>
	</div>


### Step 2.4 - Add translation for role in the file: src/main/webapp/i18n/en/register.json
	"roles": "Roles"

### Step 2.5 - Add controller logic:

        vm.authority=null;
        vm.authorities = ['ROLE_USER', 'ROLE_MANAGER'];
        
        function register () {
            if (vm.registerAccount.password !== vm.confirmPassword) {
                vm.doNotMatch = 'ERROR';
            } else {
            	
            	vm.registerAccount.authorities=[];
            	vm.registerAccount.authorities.push(vm.authority); 

