use diesel::{
    query_builder::{AstPass, Query, QueryFragment},
    query_dsl::LoadQuery,
    sql_types::BigInt,
    sqlite::Sqlite,
    QueryId,
    QueryResult,
    RunQueryDsl,
    SqliteConnection,
};

pub enum PaginationError {
    InvalidLimit,
}

#[derive(Debug, Clone, Copy, QueryId)]
pub struct Paginated<T> {
    query: T,
    offset: i64,
    limit: i64,
}

pub trait Paginate
where
    Self: RunQueryDsl<SqliteConnection>,
{
    fn paginate(self, number: i64, limit: i64) -> Result<Paginated<Self>, PaginationError>;
}

impl<T> Paginate for T
where
    T: RunQueryDsl<SqliteConnection>,
{
    fn paginate(self, number: i64, limit: i64) -> Result<Paginated<Self>, PaginationError> {
        if limit <= 0 {
            return Err(PaginationError::InvalidLimit);
        }

        Ok(Paginated {
            query: self,
            offset: number * limit,
            limit,
        })
    }
}

impl<T> Paginated<T>
where
    T: RunQueryDsl<SqliteConnection>,
{
    pub fn load<'a, U>(self, conn: &mut SqliteConnection) -> QueryResult<(Vec<U>, i64)>
    where
        Self: LoadQuery<'a, SqliteConnection, (U, i64)>,
    {
        let limit = self.limit;

        let results = RunQueryDsl::load::<(U, i64)>(self, conn)?;

        let total_pages = (results.get(0).map(|x| x.1).unwrap_or(0) + limit - 1) / limit;

        let records = results.into_iter().map(|x| x.0).collect();

        Ok((records, total_pages))
    }
}

impl<T: Query> Query for Paginated<T>
where
    T: RunQueryDsl<SqliteConnection>,
{
    type SqlType = (T::SqlType, BigInt);
}

impl<T> RunQueryDsl<SqliteConnection> for Paginated<T> where T: RunQueryDsl<SqliteConnection> {}

impl<T> QueryFragment<Sqlite> for Paginated<T>
where
    T: QueryFragment<Sqlite> + RunQueryDsl<SqliteConnection>,
{
    fn walk_ast<'b>(&'b self, mut out: AstPass<'_, 'b, Sqlite>) -> QueryResult<()> {
        out.push_sql("SELECT *, COUNT(*) OVER () FROM (");

        self.query.walk_ast(out.reborrow())?;

        out.push_sql(") t LIMIT ");

        out.push_bind_param::<BigInt, _>(&self.limit)?;

        out.push_sql(" OFFSET ");

        out.push_bind_param::<BigInt, _>(&self.offset)?;

        Ok(())
    }
}
