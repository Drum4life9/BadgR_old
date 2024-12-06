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
-- Table structure for table `userreq`
--

DROP TABLE IF EXISTS `userreq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userreq` (
  `userID` int NOT NULL,
  `badgeTableID` int NOT NULL,
  `reqNum` int DEFAULT NULL,
  `isCompleted` tinyint(1) DEFAULT NULL,
  KEY `userID_idx` (`userID`),
  KEY `badgeTableID_idx` (`badgeTableID`),
  CONSTRAINT `badgeTableID` FOREIGN KEY (`badgeTableID`) REFERENCES `badgetable` (`badgeTableID`),
  CONSTRAINT `userID` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userreq`
--

LOCK TABLES `userreq` WRITE;
/*!40000 ALTER TABLE `userreq` DISABLE KEYS */;
INSERT INTO `userreq` VALUES (1160,1,1,0),(1160,1,2,0),(1160,1,3,0),(1160,1,4,0),(1160,1,5,0),(1160,1,6,0),(1160,1,1,0),(1160,1,2,0),(1160,1,3,0),(1160,1,4,0),(1160,1,5,0),(1160,1,6,0),(1160,1,1,0),(1160,1,2,0),(1160,1,3,0),(1160,1,4,0),(1160,1,5,0),(1160,1,6,0),(1160,130,1,1),(1160,130,2,1),(1160,130,3,1),(1160,130,4,1),(1160,130,5,0),(1160,130,6,0),(1160,20,1,0),(1160,20,2,0),(1160,20,3,0),(1160,20,4,0),(1160,20,5,0),(1160,20,6,0),(1160,20,7,0),(1160,20,8,0),(1160,20,9,0),(1160,20,10,0),(1159,4,1,0),(1159,4,2,0),(1159,4,3,0),(1159,4,4,0),(1159,4,5,0),(1159,4,6,0),(1159,4,7,0),(1159,4,8,0),(1159,4,9,0),(1160,1,1,0),(1160,1,2,0),(1160,1,3,0),(1160,1,4,0),(1160,1,5,0),(1160,1,6,0),(1160,10,1,0),(1160,10,2,0),(1160,10,3,0),(1160,10,4,0),(1160,10,5,0),(1160,10,6,0),(1160,10,7,0);
/*!40000 ALTER TABLE `userreq` ENABLE KEYS */;
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
