-- MySQL dump 10.13  Distrib 8.0.27, for Win64 (x86_64)
--
-- Host: localhost    Database: users
-- ------------------------------------------------------
-- Server version	8.0.27

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `badgetable`
--

DROP TABLE IF EXISTS `badgetable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `badgetable` (
  `badgeTableID` int NOT NULL AUTO_INCREMENT,
  `badgeName` varchar(50) DEFAULT NULL,
  `isEagleReq` tinyint(1) DEFAULT NULL,
  `numReqs` int DEFAULT NULL,
  PRIMARY KEY (`badgeTableID`),
  UNIQUE KEY `BadgeTableID_UNIQUE` (`badgeTableID`)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `badgetable`
--

LOCK TABLES `badgetable` WRITE;
/*!40000 ALTER TABLE `badgetable` DISABLE KEYS */;
INSERT INTO `badgetable` VALUES (1,'American Business',0,6),(2,'American Cultures',0,5),(3,'American Heritage',0,6),(4,'American Labor',0,9),(5,'Animal Science',0,7),(6,'Animation',0,5),(7,'Archaeology',0,11),(8,'Archery',0,5),(9,'Architecture',0,5),(10,'Art',0,7),(11,'Astronomy',0,9),(12,'Athletics',0,6),(14,'Automotive Maintenance',0,12),(15,'Aviation',0,5),(16,'Backpacking',0,11),(17,'Basketry',0,3),(18,'Bird Study',0,11),(19,'Bugling',0,5),(20,'Camping',1,10),(21,'Canoeing',0,13),(23,'Chemistry',0,7),(24,'Chess',0,6),(26,'Citizenship in the Community',1,8),(27,'Citizenship in the Nation',1,8),(28,'Citizenship in the World',1,7),(29,'Climbing',0,12),(30,'Coin Collecting',0,10),(31,'Collections',0,7),(32,'Communication',1,9),(33,'Composite Materials',0,6),(35,'Cooking',1,7),(36,'Crime Prevention',0,9),(37,'Cycling',0,7),(38,'Dentistry',0,7),(39,'Digital Technology',0,9),(40,'Disabilities Awareness',0,7),(41,'Dog Care',0,10),(42,'Drafting',0,7),(43,'Electricity',0,11),(44,'Electronics',0,6),(45,'Emergency Preparedness',1,9),(46,'Energy',0,5),(47,'Engineering',0,9),(48,'Entrepreneurship',0,6),(49,'Environmental Science',1,6),(51,'Family Life',1,7),(52,'Farm Mechanics',0,4),(53,'Fingerprinting',0,5),(54,'Fire Safety',0,13),(55,'First Aid',1,14),(56,'Fish and Wildlife Management',0,8),(57,'Fishing',0,10),(58,'Fly Fishing',0,11),(59,'Forestry',0,8),(60,'Game Design',0,8),(61,'Gardening',0,8),(62,'Genealogy',0,9),(64,'Geology',0,3),(65,'Golf',0,8),(66,'Graphic Arts',0,7),(68,'Hiking',1,6),(69,'Home Repairs',0,6),(70,'Horsemanship',0,11),(71,'Indian Lore',0,5),(72,'Insect Study',0,12),(73,'Inventing',0,9),(74,'Journalism',0,5),(75,'Kayaking',0,8),(76,'Landscape Architecture',0,5),(77,'Law',0,11),(78,'Leatherwork',0,5),(79,'Lifesaving',1,17),(80,'Mammal Study',0,5),(81,'Medicine',0,10),(82,'Metalwork',0,3),(84,'Model Design and Building',0,6),(85,'Motorboating',0,3),(86,'Moviemaking',0,4),(87,'Music',0,5),(88,'Nature',0,5),(89,'Nuclear Science',0,8),(90,'Oceanography',0,9),(91,'Orienteering',0,10),(92,'Painting',0,8),(94,'Personal Fitness',1,9),(95,'Personal Management',1,10),(96,'Pets',0,4),(97,'Photography',0,8),(98,'Pioneering',0,13),(99,'Plant Science',0,8),(100,'Plumbing',0,4),(101,'Pottery',0,8),(102,'Programming',0,6),(103,'Public Health',0,7),(104,'Public Speaking',0,5),(105,'Pulp and Paper',0,8),(106,'Radio',0,5),(107,'Railroading',0,8),(108,'Reading',0,6),(109,'Reptile and Amphibian Study',0,10),(110,'Rifle Shooting',0,2),(111,'Robotics',0,7),(112,'Rowing',0,9),(113,'Safety',0,8),(114,'Salesmanship',0,7),(115,'Scholarship',0,3),(116,'Scouting Heritage',0,8),(118,'Sculpture',0,3),(120,'Shotgun Shooting',0,2),(122,'Signs Signals and Codes',0,10),(123,'Skating',0,2),(124,'Small Boat Sailing',0,5),(125,'Snow Sports',0,7),(126,'Soil and Water Conservation',0,7),(127,'Space Exploration',0,4),(128,'Sports',0,5),(129,'Stamp Collecting',0,8),(130,'Sustainability',1,6),(131,'Surveying',0,4),(132,'Swimming',1,8),(133,'Textile',0,6),(134,'Theater',0,5),(136,'Traffic Safety',0,5),(137,'Truck Transportation',0,10),(138,'Veterinary Medicine',0,6),(139,'Water Sports',0,7),(140,'Weather',0,11),(141,'Welding',0,7),(142,'Whitewater',0,12),(143,'Wilderness Survival',0,12),(144,'Wood Carving',0,7),(145,'Woodwork',0,7);
/*!40000 ALTER TABLE `badgetable` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-08-28  9:06:59
