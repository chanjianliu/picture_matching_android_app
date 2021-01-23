The game begins by having the player input a url link of a photo website, the link is then scrapped using the jsoup library. If the link is valid, 20 images from the website will be downloaded. The player can then choose 6 photos to begin the game, a stopwatch begins to keep track of the time use to complete the game, and sound effects are added for matched/mismatched pictures. After completing the game, the player is shown his time result, and a record of "best-time" to keep track of the shortest time used to complete the game. Player can then choose to replay the game with the previously selected pictures or return to main menu and input a new photo url link.


Deliverable By:
1. AFRIN Rukaya (A0113802W)
2. CHAN Jian Liu (A0226741H)
3. LIU Lei (A0214899L)
4. RAMAKRISHNAN Niveditha (A0214867W)
5. XIAO Changwei (A0226757U)
6. ZHANG Hongduo (A0226744B)
7. ZHOU Yanjun (A0226701N)
8. ZHU Haokun (A0226723H)


3rd-Party Library Used:
1. jsoup


Features:
1. If user insert a new link and fetch while app is still downloading 20 images. The download will stop and restart with the new link. 
2. Sound effects (click sounds, match or mismatch sounds, and different grading of results sounds)
3. 3-2-1 countdown timer to get player ready for the game
4. Grading system based on player’s timing
5. Best-time indicator (using SharedPreferences to remember the lowest time user took to complete game)


Team Contribution:
1. AFRIN Rukaya: XML Layouts and Music features
2. CHAN Jian Liu: image downloading (asynchronously), validation of correct url link input, image selection to activate GamePlay activity, support for app debugging 
3. LIU Lei:Progress Bar, Layout of MainActivity and Count down function
4. RAMAKRISHNAN Niveditha: XML Layouts, Results grading and Music features
5. XIAO Changwei : Game Timer and Image Download
6. ZHANG Hongduo : Progress Bar and Layout of MainActivity  and Count down function
7. ZHOU Yanjun : Scraped and retrieved images from website using Jsoup library; Implemented images matching logic after user selected 6 images 
8. ZHU Haokun : image downloading, selection of images
