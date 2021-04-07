
 #install dependancies for acumos package

install.packages("RProtoBuf", repos="http://cloud.r-project.org",dependancies=T)
install.packages("httr", repos="http://cloud.r-project.org",dependancies=T)
install.packages("jsonlite", repos="http://cloud.r-project.org",dependancies=T)
install.packages("Rserve", repos="http://cloud.r-project.org",dependancies=T)

#install acumos package in R

remotes::install_github("acumos/acumos-r-client", subdir="acumos-package")
