import androidx.room.Room.databaseBuilder
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import us.mikeandwan.photos.database.MawDatabase
import us.mikeandwan.photos.database.migrations.MIGRATION_1_2
import us.mikeandwan.photos.database.migrations.MIGRATION_2_3
import us.mikeandwan.photos.database.migrations.MIGRATION_3_4
import java.io.IOException

class RoomMigrationTests {
    private val TEST_DB = "migration-test"

    private val ALL_MIGRATIONS = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4
    )

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MawDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create earliest version of the database.
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        // Open latest version of the database. Room validates the schema
        // once all migrations execute.
        databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MawDatabase::class.java,
            TEST_DB
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase.close()
        }
    }
}
