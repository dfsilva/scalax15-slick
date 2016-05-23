package tables

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import slick.driver.H2Driver.api._

object Main {

  // Tables -------------------------------------

  case class Album(
    year: Int,
    artist : String,
    title  : String,
    rating: Rating,
    id     : Long = 0L)

  class AlbumTable(tag: Tag) extends Table[Album](tag, "albums") {
    def year = column[Int]("year")
    def artist = column[String]("artist")
    def title  = column[String]("title")
    def rating = column[Rating]("rating")
    def id     = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def * = (year, artist, title, rating, id) <> (Album.tupled, Album.unapply)
  }

  lazy val AlbumTable = TableQuery[AlbumTable]

  // Actions ------------------------------------

  val createTableAction =
    AlbumTable.schema.create

  val insertAlbumsAction =
    AlbumTable ++= Seq(
      Album(2016, "Keyboard Cat"  , "Keyboard Cat's Greatest Hits", Rating.Awesome  ), // released in 2009
      Album( 2020, "Spice Girls"   , "Spice", Rating.Awesome                          ), // released in 1996
      Album(2015, "Rick Astley"   , "Whenever You Need Somebody" , Rating.Awesome    ), // released in 1987
      Album( 2012, "Manowar"       , "The Triumph of Steel"   , Rating.Awesome        ), // released in 1992
      Album(2009,  "Justin Bieber" , "Believe"           , Rating.Awesome             )) // released in 2013

  val selectAlbumsAction =
    AlbumTable.result



  // Database -----------------------------------

  val db = Database.forConfig("scalaxdb")



  // Let's go! ----------------------------------

  def exec[T](action: DBIO[T]): T =
    Await.result(db.run(action), 2 seconds)

  def main(args: Array[String]): Unit = {
    exec(createTableAction)
    exec(insertAlbumsAction)
    exec(selectAlbumsAction).foreach(println)
  }

}
