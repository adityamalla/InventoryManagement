package com.safetystratus.inventorymanagement;
public class QueryConstants {
    public static final String TABLE_NAME_SITE_USERS = "site_users";
    public static final String TABLE_NAME_FI_LOCATIONS = "fi_locations";
    public static final String TABLE_NAME_FI_FACIL_ROOMS = "fi_facil_rooms";
    public static final String TABLE_NAME_FI_ROOM_DEPT = "fi_room_dept";
    public static final String TABLE_NAME_FI_ROOM_TYPES = "fi_room_types";
    public static final String TABLE_NAME_FI_FACILITIES = "fi_facilities";
    public static final String TABLE_NAME_OT_ORGANIZATION = "ot_organization";
    public static final String TABLE_NAME_OT_DEPARTMENT = "ot_department";
    public static final String TABLE_NAME_FI_ROOM_ROSTER = "fi_room_roster";
    public static final String TABLE_NAME_CHEMICAL_INVENTORY = "chemical_inventory";
    public static final String TABLE_NAME_LABELS = "labels";
    public static final String TABLE_NAME_SETTINGS = "settings";
    public static final String TABLE_NAME_SCANNED_DATA = "scanned_data";
    public static final String TABLE_NAME_MENU_CATEGORIES = "menu_categories";
    public static final String TABLE_NAME_MENU_ITEMS = "menu_items";

    public static String SQL_CREATE_CHEMICAL_INVENTORY = "CREATE TABLE IF NOT EXISTS chemical_inventory\n" +
            "(id integer PRIMARY KEY NOT NULL,\n" +
            "        code text ,\n" +
            "    \tsec_code text,\n" +
            "    \tname text,\n" +
            "    \topened_date text,\n" +
            "    \treceipt_date text,\n" +
            "    \tcreate_date text,\n" +
            "    \tobject_id integer,\n" +
            "    \t room_id integer,\n" +
            "        object_table text ,\n" +
            "    \tmodified_user_id numeric,\n" +
            "        modified_date text,\n" +
            "        lot text ,\n" +
            "    \tlast_test_date text,\n" +
            "    \tcreate_user_id integer NOT NULL,\n" +
            "    \texpiration_date text)";
    public static String SQL_CREATE_SETTINGS = "CREATE TABLE IF NOT EXISTS settings\n" +
            "(\n" +
            "    setting character varying(32) PRIMARY KEY NOT NULL,\n" +
            "    value text NOT NULL\n" +
            ")";
    public static String SQL_CREATE_MENU_CATEGORIES = "CREATE TABLE IF NOT EXISTS menu_categories\n" +
            "(\n" +
            "    id integer PRIMARY KEY NOT NULL,\n" +
            "    name character varying(64),\n" +
            "    sort integer DEFAULT 0\n" +
            ")";
    public static String SQL_CREATE_TABLE_MENU_ITEMS = "CREATE TABLE IF NOT EXISTS menu_items\n" +
            "(\n" +
            "    id integer primary key NOT NULL,\n" +
            "    name character varying(64),\n" +
            "    descr character varying(256),\n" +
            "    m_cat_id integer,\n" +
            "    sort integer DEFAULT 0\n" +
            ")";
    public static String SQL_CREATE_LABELS = "CREATE TABLE IF NOT EXISTS labels\n" +
            "(\n" +
            "    label character varying(32) NOT NULL,\n" +
            "    value character varying(32) NOT NULL,\n" +
            "    last_updated timestamp DEFAULT CURRENT_TIMESTAMP\n" +
            ")";
    public static String SQL_CREATE_FI_ROOM_ROSTER = "CREATE TABLE IF NOT EXISTS fi_room_roster    \n" +
            "             (    \n" +
            "                 room_id integer NOT NULL,    \n" +
            "                 user_id integer NOT NULL,    \n" +
            "                 type character varying(32),    \n" +
            "                 latch integer NOT NULL,    \n" +
            "                 roster_type_id integer NOT NULL,\n" +
            "\t\t\t\t CONSTRAINT fi_room_roster_pkey PRIMARY KEY (room_id, user_id, roster_type_id),\n" +
            "\t\t\t\t CONSTRAINT fi_room_roster_room_id_fkey FOREIGN KEY (room_id)\n" +
            "\t\t\t\t REFERENCES fi_facil_rooms (id) MATCH SIMPLE\n" +
            "\t\t\t\t ON UPDATE NO ACTION\n" +
            "\t\t\t\t ON DELETE NO ACTION,\n" +
            "\t\t\t\t CONSTRAINT fi_room_roster_user_id_fkey FOREIGN KEY (user_id)\n" +
            "\t\t\t\t REFERENCES site_users (user_id) MATCH SIMPLE\n" +
            "\t\t\t\t ON UPDATE NO ACTION\n" +
            "\t\t\t\t ON DELETE NO ACTION\n" +
            "             )";
    public static String SQL_CREATE_TABLE_OT_DEPARTMENTS = "CREATE TABLE IF NOT EXISTS ot_department    \n" +
            "             (    \n" +
            "                 id integer PRIMARY KEY NOT NULL,    \n" +
            "                 dept_cd varchar(16),    \n" +
            "                 name varchar(128) NOT NULL,    \n" +
            "                 short_name varchar(32),    \n" +
            "                 org_id integer NOT NULL,    \n" +
            "                 status varchar(16) NOT NULL,\n" +
            "                 CONSTRAINT ot_department_org_id_fkey FOREIGN KEY (org_id)\n" +
            "\t\t\t\t REFERENCES ot_organization (id) MATCH SIMPLE\n" +
            "\t\t\t\t ON UPDATE NO ACTION\n" +
            "\t\t\t\t ON DELETE NO ACTION\n" +
            "             )";

    public static String SQL_CREATE_TABLE_SITE_USERS = "CREATE TABLE IF NOT EXISTS site_users\n" +
            "(\n" +
            "    user_id integer PRIMARY KEY NOT NULL,\n" +
            "    username varchar(64) NOT NULL,\n" +
            "    firstname varchar(64),\n" +
            "    lastname varchar(64),\n" +
            "    email_address varchar(128) NOT NULL,\n" +
            "    active varchar(8),\n" +
            "    phone varchar(16),\n" +
            "\tprimary_group varchar(128)\n" +
            ")";

    public static String SQL_CREATE_TABLE_FI_LOCATIONS = "CREATE TABLE IF NOT EXISTS fi_locations\n" +
            "(\n" +
            "    id integer PRIMARY KEY NOT NULL ,\n" +
            "    location_cd varchar(16),\n" +
            "    name varchar(128) NOT NULL,\n" +
            "    short_name varchar(32),\n" +
            "    status varchar(16)\n" +
            ")";

    public static String SQL_CREATE_TABLE_SCANNED_DATA = "CREATE TABLE IF NOT EXISTS scanned_data\n" +
            "(\n" +
            "    id integer PRIMARY KEY NOT NULL ,\n" +
            "    location_id integer NOT NULL,\n" +
            "    room_id integer NOT NULL,\n" +
            "    inventory_id integer NOT NULL\n" +
            ")";


    public static String SQL_CREATE_FI_FACIL_ROOMS = "CREATE TABLE IF NOT EXISTS fi_facil_rooms    \n" +
            "             (    \n" +
            "                 id integer PRIMARY KEY NOT NULL,    \n" +
            "                 facil_id integer NOT NULL,    \n" +
            "                 room text NOT NULL,    \n" +
            "                 img_src character varying(128),    \n" +
            "                 notes character varying(256),    \n" +
            "                 area double precision,    \n" +
            "                 type_id integer,    \n" +
            "                 status character varying(16),\n" +
            "                 CONSTRAINT fi_facil_rooms_facil_id_fkey FOREIGN KEY (facil_id)\n" +
            "                 REFERENCES fi_facilities (id) MATCH SIMPLE\n" +
            "                 ON UPDATE NO ACTION\n" +
            "                 ON DELETE NO ACTION,\n" +
            "                 CONSTRAINT fi_facil_rooms_type_fkey FOREIGN KEY (type_id)\n" +
            "                 REFERENCES fi_room_types (type_id) MATCH SIMPLE\n" +
            "                 ON UPDATE NO ACTION\n" +
            "                 ON DELETE NO ACTION\n" +
            "             )";


    public static String SQL_CREATE_FI_ROOM_DEPT = "CREATE TABLE IF NOT EXISTS fi_room_dept    \n" +
            "             (    \n" +
            "                 room_id integer NOT NULL,    \n" +
            "                 dept_id integer NOT NULL,\n" +
            "\t\t\t\t CONSTRAINT fi_room_dept_pkey PRIMARY KEY (room_id, dept_id),\n" +
            "\t\t\t\t CONSTRAINT fi_room_dept_dept_id_fkey FOREIGN KEY (dept_id)\n" +
            "\t\t\t\t\tREFERENCES ot_department (id) MATCH SIMPLE\n" +
            "\t\t\t\t\tON UPDATE NO ACTION\n" +
            "\t\t\t\t\tON DELETE NO ACTION,\n" +
            "\t\t\t\t CONSTRAINT fi_room_dept_room_id_fkey FOREIGN KEY (room_id)\n" +
            "\t\t\t\t\tREFERENCES fi_facil_rooms (id) MATCH SIMPLE\n" +
            "\t\t\t\t\tON UPDATE NO ACTION\n" +
            "\t\t\t\t\tON DELETE NO ACTION\n" +
            "             )";

    public static String SQL_CREATE_FI_ROOM_TYPES = "CREATE TABLE IF NOT EXISTS fi_room_types\n" +
            "(\n" +
            "    type character varying(128),\n" +
            "    required text,\n" +
            "    cycle_duration integer,\n" +
            "    cycle_default text,\n" +
            "    type_id integer PRIMARY KEY NOT NULL,\n" +
            "    cycle_buffer integer DEFAULT 0,\n" +
            "    status character varying(16) NOT NULL\n" +
            ")";

    public static String SQL_CREATE_FI_FACILITIES = "CREATE TABLE IF NOT EXISTS fi_facilities    \n" +
            "             (    \n" +
            "                 id integer PRIMARY KEY NOT NULL,    \n" +
            "                 name character varying(64) NOT NULL,    \n" +
            "                 short_name character varying(32),    \n" +
            "                 location_id integer NOT NULL DEFAULT 1,    \n" +
            "                 status character varying(16) DEFAULT 'active',\n" +
            "\t\t\t\t CONSTRAINT fi_facilities_location_id_fkey FOREIGN KEY (location_id)\n" +
            "   \t\t\t\t REFERENCES fi_locations (id) MATCH SIMPLE\n" +
            "\t\t\t\t ON UPDATE NO ACTION\n" +
            "\t\t\t\t ON DELETE NO ACTION\n" +
            "             )";

    public static String SQL_CREATE_OT_ORGANIZATION = "CREATE TABLE IF NOT EXISTS ot_organization\n" +
            "(\n" +
            "    id integer PRIMARY KEY NOT NULL,\n" +
            "    org_cd character varying(16),\n" +
            "    name character varying(128) NOT NULL,\n" +
            "    short_name character varying(32),\n" +
            "    location_id integer,\n" +
            "    status character varying(16) NOT NULL DEFAULT 'active'\n" +
            ")";
}

