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
-- Table structure for table `userbadges`
--

DROP TABLE IF EXISTS `userbadges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userbadges` (
  `badgeTableID` int NOT NULL,
  `userID` int NOT NULL,
  `isCompleted` tinyint(1) DEFAULT NULL,
  KEY `badgeTableID_idx` (`badgeTableID`),
  KEY `userID_idx` (`userID`),
  CONSTRAINT `bTID` FOREIGN KEY (`badgeTableID`) REFERENCES `badgetable` (`badgeTableID`),
  CONSTRAINT `uID` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userbadges`
--

LOCK TABLES `userbadges` WRITE;
/*!40000 ALTER TABLE `userbadges` DISABLE KEYS */;
INSERT INTO `userbadges` VALUES (18,1160,1),(65,1160,1),(54,1160,1),(51,1160,1),(145,1160,1),(137,1160,1),(138,1160,1),(130,1160,0),(27,1196,1),(54,1196,1),(145,1196,1),(144,1196,1),(138,1196,1),(134,1196,1),(132,1196,1),(131,1196,1),(125,1196,1),(127,1196,1),(122,1196,1),(123,1196,1),(124,1196,1),(126,1196,1),(56,1196,1),(57,1196,1),(58,1196,1),(60,1196,1),(61,1196,1),(11,1196,1),(9,1193,1),(1,1159,1),(15,1160,1),(20,1160,0),(4,1159,0),(6,1159,1),(9,1159,1),(1,1160,0),(134,1160,1),(10,1160,0);
/*!40000 ALTER TABLE `userbadges` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-08-28  9:06:58
