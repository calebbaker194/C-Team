BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "salesrep" (
	"salesrep_id"	integer,
	"sr_fname"	text,
	"sr_lname"	text,
	"sr_commission"	real,
	PRIMARY KEY("salesrep_id")
);
CREATE TABLE IF NOT EXISTS "invoice" (
	"invc_id"	integer,
	"invc_number"	text,
	"invc_sr_id"	integer,
	"invc_amount"	INTEGER,
	PRIMARY KEY("invc_id")
);
INSERT INTO "salesrep" VALUES (1,'Terissa','Mathis',0.055);
INSERT INTO "salesrep" VALUES (2,'Lia','Adam',0.045);
INSERT INTO "salesrep" VALUES (3,'Tyler','Patton',0.015);
INSERT INTO "salesrep" VALUES (4,'Kamil','Hardy',0.112);
INSERT INTO "salesrep" VALUES (5,'Thalia','Booth',0.05);
INSERT INTO "salesrep" VALUES (6,'Rick','Adams',0.034);
INSERT INTO "salesrep" VALUES (7,'Lainey','Richards',0.022);
INSERT INTO "invoice" VALUES (1,'CT-01',1,57089.21);
INSERT INTO "invoice" VALUES (2,'CT-02',5,2187.07);
INSERT INTO "invoice" VALUES (3,'CT-03',3,5484.86);
INSERT INTO "invoice" VALUES (4,'CT-04',5,56312);
INSERT INTO "invoice" VALUES (5,'CT-05',5,2637.48);
INSERT INTO "invoice" VALUES (6,'CT-06',6,22015.13);
INSERT INTO "invoice" VALUES (7,'CT-07',7,72618.09);
INSERT INTO "invoice" VALUES (8,'CT-08',2,77258.92);
INSERT INTO "invoice" VALUES (9,'CT-09',4,79553.88);
INSERT INTO "invoice" VALUES (10,'CT-10',4,16157.03);
INSERT INTO "invoice" VALUES (11,'CT-11',5,32964.7);
INSERT INTO "invoice" VALUES (12,'CT-12',1,11148.84);
INSERT INTO "invoice" VALUES (13,'CT-13',4,80283.79);
COMMIT;
