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
-- Table structure for table `recentactivity`
--

DROP TABLE IF EXISTS `recentactivity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recentactivity` (
  `notifID` int NOT NULL AUTO_INCREMENT,
  `newNotif` tinyint(1) DEFAULT NULL,
  `notifType` varchar(10) DEFAULT NULL,
  `userID` int NOT NULL,
  `troop` int DEFAULT NULL,
  `badgeTableID` int NOT NULL,
  PRIMARY KEY (`notifID`),
  UNIQUE KEY `notifID_UNIQUE` (`notifID`),
  KEY `recentactivity_ibfk_2_idx` (`badgeTableID`),
  KEY `userIDrecentAct` (`userID`),
  CONSTRAINT `userIDrecentAct` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recentactivity`
--

LOCK TABLES `recentactivity` WRITE;
/*!40000 ALTER TABLE `recentactivity` DISABLE KEYS */;
INSERT INTO `recentactivity` VALUES (77,1,'n',1195,12,-1),(78,1,'n',1196,12,-1),(79,1,'b',1196,12,124),(80,1,'b',1196,12,61),(81,1,'b',1196,12,122),(82,1,'b',1196,12,57),(83,1,'b',1196,12,54),(84,1,'b',1196,12,132),(85,1,'b',1196,12,58),(86,1,'b',1196,12,144),(87,1,'b',1196,12,125),(88,1,'b',1196,12,131),(89,1,'b',1196,12,60),(90,1,'b',1196,12,126),(91,1,'b',1196,12,11),(92,1,'b',1196,12,123),(93,1,'b',1196,12,27),(94,1,'b',1196,12,56),(95,1,'b',1196,12,127),(96,1,'b',1196,12,134),(97,1,'b',1196,12,138),(98,1,'b',1196,12,145);
/*!40000 ALTER TABLE `recentactivity` ENABLE KEYS */;
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
