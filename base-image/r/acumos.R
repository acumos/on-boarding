
 #install dependancies for acumos package

install.packages("RProtobuf", repos="http://cloud.r-project.org",dependancies=T)
install.packages("httr", repos="http://cloud.r-project.org",dependancies=T)
install.packages("jsonlite", repos="http://cloud.r-project.org",dependancies=T)
install.packages("Rserve", repos="http://cloud.r-project.org",dependancies=T)

#install acumos package in R

install.packages("https://github.com/sambaala/R-acumos/archive/v0.3-0.tar.gz", repos=NULL)
