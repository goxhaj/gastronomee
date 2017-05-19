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

### Step 2.1 - Create a file on: src/main/resources/config/liquibase/changelog/load_data.xml with contents:

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

### Step 3 - Generate the application from jdl(in the root folder of the project is located gastronomee-jdl.jh file):
		jhipster-uml gastronomee-jdl.jh
		
### Step 3 - Improt data from csv using loadData.xml file (in src/main/resources/config/liquibase):
#### 3.1 - Add csv files:
		-regions.csv
		-countries.csv
		-dish_categories.csv
		-ingredients.csv
		-managers.csv
		-managers_authorities.csv
		-menus.csv
		-restaurants.csv
		-dishes.csv
		
#### 3.2 - Import them using liquibase (src/main/resources/config/liquibase/changelog/loadData.xml) add these lines inside databaseChangeLog tags:
	<!-- create 6 managers -->
	<changeSet author="ardi" id="load_user_managers">
		<loadData encoding="UTF-8"
	                  file="config/liquibase/managers.csv"
	                  separator=";"
	                  tableName="jhi_user">
	    </loadData>
    </changeSet>
    
    <!-- add the 6 managers created previously to manager role -->
    <changeSet author="ardi" id="load_user_manager_authorities">
		<loadData encoding="UTF-8"
	                  file="config/liquibase/managers_authorities.csv"
	                  separator=";"
	                  tableName="jhi_user_authority">
	    </loadData>
    </changeSet>
	
	<changeSet author="ardi" id="load_countries">
		<loadData encoding="UTF-8"
	                  file="config/liquibase/countries.csv"
	                  separator=","
	                  tableName="country">
	    </loadData>
    </changeSet>
    
    <changeSet author="ardi" id="load_regions">
		<loadData encoding="UTF-8"
	                  file="config/liquibase/regions.csv"
	                  separator=","
	                  tableName="region">
	    </loadData>
    </changeSet>
    
    <changeSet author="ardi" id="load_dish_categories">
		<loadData encoding="UTF-8"
	                  file="config/liquibase/dish_categories.csv"
	                  separator=","
	                  tableName="dish_category">
	    </loadData>
    </changeSet>
    
    <changeSet author="ardi" id="load_ingredients">
		<loadData encoding="UTF-8"
	                  file="config/liquibase/ingredients.csv"
	                  separator=","
	                  tableName="ingredient">
	    </loadData>
    </changeSet>
    
   <changeSet author="ardi" id="load_restaurants">
		<loadData encoding="UTF-8"
	                  file="config/liquibase/restaurants.csv"
	                  separator=";"
	                  tableName="restaurant">
	    </loadData>
    </changeSet>
    
    <changeSet author="ardi" id="load_menu">
		<loadData encoding="UTF-8"
	                  file="config/liquibase/menus.csv"
	                  separator=";"
	                  tableName="menu">
	    </loadData>
    </changeSet>
    
    <changeSet author="ardi" id="load_dishes">
		<loadData encoding="UTF-8"
	                  file="config/liquibase/dishes.csv"
	                  separator=";"
	                  tableName="dish">
	    </loadData>
    </changeSet>
		
### Step 4  - Add Jhipster Elastic search reindexer  
#### Step 4.1
	npm install -g generator-jhipster-elasticsearch-reindexer
#### Step 4.2 - Generate the app inside the root directory
	yo jhipster-elasticsearch-reindexer
####  Step 4.3 - Inject javascript into index.hml
	gulp install

### Step 5 - Fix problem when update-ing a user role from admin user management menu (there is no ROLE_MANAGER)
Added the role ROLE_MANAGER into vm.authorities array of roles in user-management.controller.js and user-management-dialog.controller.js located in the src/main/webapp/app/admin/user-management directory


